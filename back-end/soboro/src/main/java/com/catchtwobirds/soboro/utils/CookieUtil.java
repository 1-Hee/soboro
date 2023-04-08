package com.catchtwobirds.soboro.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

/**
 * 이 코드는 쿠키를 다루는 유틸리티 클래스입니다. <br>
 * 쿠키는 HTTP 요청과 응답 간에 데이터를 저장하고 전송하는 방법 중 하나입니다. 이 클래스는 쿠키를 추가, 삭제 및 가져오는 기능과 객체를 직렬화하여 문자열로 변환하는 기능을 제공합니다. <br>
 * getCookie 메서드는 주어진 이름과 일치하는 쿠키를 찾아서 Optional 객체로 반환합니다. <br>
 * addCookie 메서드는 새로운 쿠키를 생성하고 응답에 추가합니다. <br>
 * deleteCookie 메서드는 주어진 이름과 일치하는 쿠키를 찾아서 삭제합니다. <br>
 * serialize 메서드는 객체를 직렬화하여 Base64 인코딩된 문자열로 반환합니다. <br>
 * deserialize 메서드는 Base64 디코딩된 쿠키 값을 역직렬화하여 주어진 클래스 형식으로 반환합니다. <br>
 * 이 클래스는 OAuth2 인증 흐름에서 인증 요청과 리다이렉트 URI를 쿠키에 저장하고 검색하는 데 사용됩니다. <br>
 * 인증 요청이 필요하지 않은 경우 쿠키를 삭제하거나 만료시간을 0으로 설정합니다.
 */

@Slf4j
@Component
public class CookieUtil {

    public static Optional<Cookie> getCookie(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        log.info("getCookie cookie : {} ", (Object) cookies);
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public static String getRefreshTokenCookie(HttpServletRequest request) {
        log.info("request.getCookies() : {} ", request.getCookies().length);
        for (Cookie cookie : request.getCookies()) {
            log.info("getRefreshTokenCookie | Cookie name = {}, Cookie Value = {}", cookie.getName(),cookie.getValue());
            if (cookie.getName().equals("refresh_token")) {
                return cookie.getValue();
            }
        }
//        throw new JwtException("RefreshToken is invaild");
        return null;
    }


    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        log.info("addCookie");
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);

        response.addCookie(cookie);
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String name) {
        log.info("deleteCookie");
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    cookie.setValue("");
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }
    }

    public static String serialize(Object obj) {
        return Base64.getUrlEncoder()
                .encodeToString(SerializationUtils.serialize(obj));
    }

    public static <T> T deserialize(Cookie cookie, Class<T> cls) {
        return cls.cast(
                SerializationUtils.deserialize(
                        Base64.getUrlDecoder().decode(cookie.getValue())
                )
        );
    }

}
