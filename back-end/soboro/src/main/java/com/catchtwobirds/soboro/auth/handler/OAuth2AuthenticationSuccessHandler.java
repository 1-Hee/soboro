package com.catchtwobirds.soboro.auth.handler;

import com.catchtwobirds.soboro.config.properties.AppProperties;
import com.catchtwobirds.soboro.auth.entity.ProviderType;
import com.catchtwobirds.soboro.auth.entity.RoleType;
import com.catchtwobirds.soboro.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.catchtwobirds.soboro.auth.info.OAuth2UserInfo;
import com.catchtwobirds.soboro.auth.info.OAuth2UserInfoFactory;
import com.catchtwobirds.soboro.auth.token.AuthToken;
import com.catchtwobirds.soboro.auth.token.AuthTokenProvider;
import com.catchtwobirds.soboro.user.repository.UserRefreshTokenRepository;

import com.catchtwobirds.soboro.utils.CookieUtil;
import com.catchtwobirds.soboro.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static com.catchtwobirds.soboro.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.REFRESH_TOKEN;

/**
 * OAuth2로 로그인을 성공적으로 마친 사용자의 정보를 처리하는 핸들러 클래스입니다. <br>
 * 우선 해당 클래스는 SimpleUrlAuthenticationSuccessHandler 클래스를 상속하고 있으며,
 * AuthTokenProvider, AppProperties, UserRefreshTokenRepository, OAuth2AuthorizationRequestBasedOnCookieRepository 등의 클래스의 인스턴스를 생성자를 통해 주입받습니다. <br>
 * `onAuthenticationSucces`s 메소드는 로그인에 성공한 경우에 호출되는 메소드이며, `determineTargetUrl` 메소드를 통해 로그인 이후 리디렉션할 URL을 결정합니다. <br>
 * 만약, 리디렉션할 URL이 유효하지 않은 경우 예외가 발생합니다. <br>
 * `determineTargetUrl` 메소드에서는 쿠키를 통해 받아온 리디렉션 URI와 `getDefaultTargetUrl` 메소드를 통해 받아온 기본 URL 중 하나를 선택합니다. <br>
 * 이후 OAuth2 토큰, 사용자 정보, 권한 정보를 이용하여 액세스 토큰 및 리프레시 토큰을 생성하고, DB에 저장합니다. <br>
 * 이후 새로 생성된 리프레시 토큰을 쿠키에 저장하고, 액세스 토큰을 URL 파라미터에 추가하여 리디렉션할 URL을 만들어 반환합니다. <br>
 * 마지막으로, `clearAuthenticationAttributes` 메소드는 인증에 사용된 쿠키를 제거합니다.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthTokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final RedisUtil redisUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

        OidcUser user = ((OidcUser) authentication.getPrincipal());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        Collection<? extends GrantedAuthority> authorities = ((OidcUser) authentication.getPrincipal()).getAuthorities();

        RoleType roleType = hasAuthority(authorities, RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;

        // refresh token 생성
        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                userInfo.getId(),
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        // refresh 토큰 기간 설정
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // rt가져오기
        String userRefreshToken = redisUtil.getData((String) userInfo.getId());

        // 리프래쉬 토큰이 있으면 삭제합니다.
        if (userRefreshToken != null) {
            log.info("refresh token exists so delete and save new token");
            userRefreshTokenRepository.deleteById((String) userInfo.getId());
        }

        // 새로 발급하기
//        userRefreshToken = refreshToken.getToken();
        // 발급한 새 토큰을 redis에 저장함.
        redisUtil.setDataExpire((String) userInfo.getId(), refreshToken.getToken(), refreshTokenExpiry);
//        userRefreshToken = new UserRefreshToken(userInfo.getId(), refreshToken.getToken());
//        userRefreshTokenRepository.saveAndFlush(userRefreshToken);

        // 쿠키
        int cookieMaxAge = (int) refreshTokenExpiry / 60;

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
        
        // token 생성값 확인
        log.info("Oauth return access token : {}", accessToken.getToken());
        log.info("Oauth redis save refresh token : {}", refreshToken.getToken());
//        return UriComponentsBuilder.fromHttpRequest("Authrorization", )

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", accessToken.getToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }
}