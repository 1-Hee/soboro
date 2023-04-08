package com.catchtwobirds.soboro.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 해당 코드는 HTTP 요청 헤더에서 Authorization 필드를 통해 전달된 JWT 토큰을 추출하는 유틸리티 클래스인 HeaderUtil 입니다. <br>
 * HTTP 요청 헤더에서 Authorization 필드의 값은 "Bearer <JWT 토큰>" 형태로 전달됩니다. <br>
 * 따라서 HeaderUtil 클래스에서는 getAccessToken() 메소드를 통해 전달된 HttpServletRequest 객체에서 Authorization 필드를 추출하고, 해당 값이 "Bearer " 로 시작하는 경우에 대해서만 JWT 토큰 값을 추출하여 반환합니다. <br>
 * 만약 Authorization 필드가 없거나 "Bearer " 로 시작하지 않는 경우에는 null 값을 반환합니다. <br>
 * 즉, HeaderUtil.getAccessToken(request) 메소드를 호출하면 HTTP 요청 헤더에서 추출한 JWT 토큰 값을 반환합니다. <br>
 * 이 JWT 토큰은 보통 Spring Security와 같은 인증 및 권한 부여 프레임워크에서 사용됩니다.
 */

@Slf4j
@Component
public class HeaderUtil {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    public static String getAccessToken(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_AUTHORIZATION);
        log.info("Access Token (HEADER) = {}", headerValue);

        if (headerValue == null) {
            return null;
        }

        if (headerValue.startsWith(TOKEN_PREFIX)) {
            return headerValue.substring(TOKEN_PREFIX.length());
        }

        return null;
    }

    public static String getAccessTokenString(String headerValue) {
        if (headerValue == null) {
            return null;
        }

        if (headerValue.startsWith(TOKEN_PREFIX)) {
            return headerValue.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}