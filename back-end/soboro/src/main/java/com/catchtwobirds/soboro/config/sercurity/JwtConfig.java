package com.catchtwobirds.soboro.config.sercurity;

import com.catchtwobirds.soboro.auth.token.AuthTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 위 코드는 `AuthTokenProvider` 클래스를 `@Bean`으로 등록하는 스프링 `@Configuration` 클래스입니다. <br>
 * `AuthTokenProvider` 클래스는 JWT 인증 토큰을 생성하고 검증하는 데 사용됩니다.
 * `AuthTokenProvider` 클래스는 생성자를 통해 인자로 secret 키를 받아서 키를 사용하여 JWT 토큰을 생성하고 검증합니다. <br>
 * 이 클래스는 `@Value` 어노테이션을 사용하여 application.properties 또는 application.yml 파일에서
 * `jwt.secret`프로퍼티 값을 가져와 **`secret`** 변수에 할당합니다. <br>
 * 이후 `jwtProvider()` 메소드에서 `secret` 변수를 사용하여 `AuthTokenProvider` 인스턴스를 생성하고 반환합니다.
 * 이렇게 생성된 `AuthTokenProvider` 인스턴스는 애플리케이션에서 JWT 토큰을 생성하고 검증하는 데 사용됩니다.
 */

@Configuration
public class JwtConfig {
    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public AuthTokenProvider jwtProvider() {
        return new AuthTokenProvider(secret);
    }
}
