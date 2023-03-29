package com.catchtwobirds.soboro.auth.exception;

import com.catchtwobirds.soboro.common.error.response.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 이 코드는 Spring Security에서 인증되지 않은 사용자가 보호된 리소스에 접근했을 때 호출되는 AuthenticationEntryPoint의 구현체이다. <br>
 * commence() 메서드는 HttpServletResponse를 사용하여 클라이언트에게 인증 오류를 반환한다. <br>
 * 먼저, 로그를 출력하여 어떤 인증 오류가 발생했는지 확인한다.
 * 그 다음, sendError()를 사용하여 클라이언트에게 401 Unauthorized 오류를 반환한다.
 * 이 오류는 인증되지 않은 사용자가 보호된 리소스에 접근했을 때 반환되는 HTTP 상태 코드이다. <br>
 * 로그를 사용하여 서버 측에서 문제가 발생했는지 확인할 수 있으며,
 * 클라이언트 측에서는 401 오류를 통해 사용자에게 알리므로 보안 측면에서 중요한 역할을 한다.
 */

@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        log.info("RestAuthenticationEntryPoint 메서드 호출");

        // Unauthorized 응답 커스터 마이징
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        ErrorResponse result = new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", authException.getMessage());
        ErrorResponse result = new ErrorResponse("Unauthorized", authException.getMessage());
        log.info("RestAuthenticationEntryPoint ErrorResponse : {}", result);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.writeValue(response.getOutputStream(), result);
        response.setStatus(HttpServletResponse.SC_OK);
        authException.printStackTrace();
        log.info("Responding with unauthorized error. Message := {}", authException.getMessage());

//        response.sendError(
//                HttpServletResponse.SC_UNAUTHORIZED,
//                authException.getLocalizedMessage()
//        );
    }
}