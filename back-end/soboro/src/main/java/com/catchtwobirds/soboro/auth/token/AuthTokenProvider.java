package com.catchtwobirds.soboro.auth.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * 위 코드는 `AuthTokenProvider` 클래스를 정의한 Java 코드입니다. <br>
 * `AuthTokenProvider` 클래스는 생성자에서 `secret` 문자열을 받아 `key` 필드에 `Keys.hmacShaKeyFor()` 메소드를 이용해 `Key` 객체를 생성합니다. <br>
 * `createAuthToken()` 메소드는 인자로 `id`, `expiry` 등의 값을 받아 `AuthToken` 객체를 생성하여 반환합니다.
 * `createAuthToken()` 메소드는 두 개가 오버로딩 되어 있으며, 두 번째 메소드는 `role` 값을 추가로 받아 `AuthToken` 객체를 생성합니다.
 * `convertAuthToken()` 메소드는 인자로 `token` 문자열을 받아 `AuthToken` 객체로 변환하여 반환합니다.<br>
 * `getAuthentication()` 메소드는 `AuthToken` 객체를 받아 `authToken.validate()` 메소드를 호출하여 `AuthToken` 객체가 유효한지 검증합니다.
 * 검증이 완료되면 `authToken.getTokenClaims()` 메소드를 호출하여 `Claims` 객체를 생성하고, 이를 이용해 `User` 객체를 생성합니다.
 * 이때, `role` 값을 이용해 `authorities` 리스트를 생성합니다. <br>
 * 마지막으로 `UsernamePasswordAuthenticationToken` 객체를 생성하여 반환합니다.
 */

@Slf4j
public class AuthTokenProvider {

    private final Key key;
    private static final String AUTHORITIES_KEY = "role";

    public AuthTokenProvider(String secret) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public AuthToken createAuthToken(String id, Date expiry) {
        return new AuthToken(id, expiry, key);
    }

    public AuthToken createAuthToken(String id, String role, Date expiry) {
        return new AuthToken(id, role, expiry, key);
    }

    public AuthToken convertAuthToken(String token) {
        return new AuthToken(token, key);
    }

    public String getPayload(String token, String className) {
        return Jwts.parserBuilder().setSigningKey(key)
                        .build().parseClaimsJws(token).getBody().get(className,String.class);
    }

    public Authentication getAuthentication(AuthToken authToken) {

//        if(authToken.validate()) {

            Claims claims = authToken.getTokenClaims();
            Collection<? extends GrantedAuthority> authorities =
                    Arrays.stream(new String[]{claims.get(AUTHORITIES_KEY).toString()})
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());

            log.info("claims subject := [{}]", claims.getSubject());
            User principal = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(principal, authToken, authorities);
//        }
//        else {
//            throw new TokenValidFailedException();
//        }
    }

    public Claims getClaims (AuthToken authToken){
        return authToken.getTokenClaims();
    }
}
