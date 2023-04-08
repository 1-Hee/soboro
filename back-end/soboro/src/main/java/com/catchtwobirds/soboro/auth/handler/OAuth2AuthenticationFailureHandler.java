package com.catchtwobirds.soboro.auth.handler;

import com.catchtwobirds.soboro.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.catchtwobirds.soboro.utils.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.catchtwobirds.soboro.auth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

/**
 * OAuth2 인증 과정에서 인증 실패 시 처리를 담당하는 클래스. <br>
 * OAuth2 인증 과정에서 인증 과정이 실패했을 때, 이 클래스는 클라이언트가 인증 요청을 보낸 리다이렉트 URL의 쿠키에서
 * `REDIRECT_URI_PARAM_COOKIE_NAME` 이름으로 저장된 값을 가져와서, 그 값으로 리다이렉트한다. <br>
 * 이 때, 리다이렉트 URL에 error 쿼리 파라미터를 추가하고,
 * 해당 파라미터 값으로는 exception 객체의 getLocalizedMessage() 메소드로 얻을 수 있는 인증 실패 메시지를 넣는다. <br>
 * 또한, OAuth2AuthorizationRequestBasedOnCookieRepository 인스턴스를 사용하여 인증 요청과 관련된 쿠키를 삭제한다.
 * 이는 인증 과정이 실패했으므로, 이전의 인증 요청과 관련된 쿠키는 더 이상 필요하지 않기 때문이다.
 */

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String targetUrl = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue)
                .orElse(("/"));

        exception.printStackTrace();

        targetUrl = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("error", exception.getLocalizedMessage())
                .build().toUriString();

        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}