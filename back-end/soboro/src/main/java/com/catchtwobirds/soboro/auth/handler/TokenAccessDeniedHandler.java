package com.catchtwobirds.soboro.auth.handler;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 위 코드는 Spring Security에서 Access Denied가 발생했을 때 처리하는 핸들러 클래스입니다. <br>
 * `TokenAccessDeniedHandler` 클래스는 `AccessDeniedHandler` 인터페이스를 구현합니다. <br>
 * `AccessDeniedHandler` 인터페이스는 인증에 실패했을 때 실행되는 메소드 `handle()`를 정의합니다.
 * `handle()` 메소드는 인자로 `HttpServletRequest`, `HttpServletResponse`, `AccessDeniedException`을 받아 처리합니다. 이 메소드는 `handlerExceptionResolver` 객체를 이용해 `AccessDeniedException` 예외를 처리합니다. <br>
 * Spring Security에서는 인증 및 인가 예외 발생 시 `AccessDeniedHandler`가 호출됩니다. 이때, `TokenAccessDeniedHandler` 클래스에서 정의한 `handle()` 메소드가 실행됩니다.
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenAccessDeniedHandler implements AccessDeniedHandler {

    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.info("TokenAccessDeniedHandler");
        response.sendError(HttpServletResponse.SC_FORBIDDEN, accessDeniedException.getMessage());
        handlerExceptionResolver.resolveException(request, response, null, accessDeniedException);
    }
}

