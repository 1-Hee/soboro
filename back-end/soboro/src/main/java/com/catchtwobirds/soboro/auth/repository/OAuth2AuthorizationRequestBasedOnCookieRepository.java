package com.catchtwobirds.soboro.auth.repository;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import com.catchtwobirds.soboro.utils.CookieUtil;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 해당 코드는 Spring Security OAuth2를 사용하여 OAuth2 인증을 구현할 때 Authorization Request를 쿠키에 저장하고 검색하는 방법을 정의하는 클래스입니다. <br>
 * OAuth2AuthorizationRequestBasedOnCookieRepository 클래스는 AuthorizationRequestRepository 인터페이스를 구현하고 있습니다.
 * 이 인터페이스는 Authorization Request를 로드하고 저장하는 메서드를 포함하고 있습니다. <br>
 * loadAuthorizationRequest(HttpServletRequest request) 메서드는 HttpServletRequest에서 OAuth2AuthorizationRequest를 검색하고 반환합니다. OAuth2AuthorizationRequest는 쿠키에 저장됩니다. <br>
 * saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) 메서드는 OAuth2AuthorizationRequest를 HttpServletResponse에 저장합니다. OAuth2AuthorizationRequest는 쿠키에 저장됩니다. <br>
 * removeAuthorizationRequest(HttpServletRequest request)와 removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) 메서드는 HttpServletRequest에서 OAuth2AuthorizationRequest를 로드하고 반환합니다. <br>
 * removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) 메서드는 OAuth2 인증 쿠키를 삭제합니다. <br>
 * 따라서 OAuth2AuthorizationRequestBasedOnCookieRepository 클래스는 OAuth2 인증을 위해 Authorization Request를 쿠키에 저장하고 검색하는 데 사용됩니다.
 */

public class OAuth2AuthorizationRequestBasedOnCookieRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    public final static String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public final static String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    public final static String REFRESH_TOKEN = "refresh_token";
    private final static int cookieExpireSeconds = 180;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtil.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtil.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            CookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            return;
        }

        CookieUtil.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtil.serialize(authorizationRequest), cookieExpireSeconds);
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            CookieUtil.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUriAfterLogin, cookieExpireSeconds);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.loadAuthorizationRequest(request);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtil.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtil.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
    }
}
