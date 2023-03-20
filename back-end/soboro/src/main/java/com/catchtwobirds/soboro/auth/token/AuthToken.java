package com.catchtwobirds.soboro.auth.token;

import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.util.Date;

/**
 * 해당 코드는 JWT 토큰을 생성하고 검증하는데 사용되는 `AuthToken` 클래스입니다. <br>
 * 생성자를 통해 `id`, `role`, `expiry`, `key` 값을 받아서 JWT 토큰을 생성합니다. <br> <br>
 * `createAuthToken()` 메서드 내부에서 JWT 빌더를 사용하여 subject, role, key, 그리고 만료 시간(expiry)을 포함시킨 JWT 토큰 문자열을 생성합니다.<br> <br>
 * `validate()` 메서드를 호출하여 JWT 토큰의 유효성을 검사하고, `getTokenClaims()` 메서드를 호출하여 JWT 토큰 내에 포함된 클레임(Claims)을 가져올 수 있습니다. <br>
 * 이 클레임은 JWT 토큰을 디코딩하면서 얻을 수 있는 정보들을 담고 있습니다. <br><br>
 * `getExpiredTokenClaims()` 메서드를 사용하면 만료된 JWT 토큰에서만 유효한 클레임 정보를 가져올 수 있습니다. <br>
 * 이 메서드는 만료된 토큰의 경우 ExpiredJwtException 예외를 발생시키기 때문에, 해당 예외를 catch하여 만료된 토큰에서만 사용 가능한 클레임 정보를 가져오도록 구현되어 있습니다.<br><br>
 * 마지막으로, 이 클래스의 주요 목적은 JWT 토큰을 생성하고, 검증하는 것이므로,
 * `AuthTokenProvider` 클래스의 인스턴스를 이용하여 `getAuthentication()` 메서드를 호출하여 `Authentication`** 객체를 생성할 수 있습니다. <br>
 * 이 객체는 스프링 시큐리티에서 인증된 사용자 정보를 담고 있습니다.
 */

@Slf4j
@ToString
@RequiredArgsConstructor
public class AuthToken {

    @Getter
    private final String token;
    private final Key key;
    private static final String AUTHORITIES_KEY = "role";

    AuthToken(String id, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, expiry);
    }

    AuthToken(String id, String role, Date expiry, Key key) {
        this.key = key;
        this.token = createAuthToken(id, role, expiry);
    }

    private String createAuthToken(String id, Date expiry) {
        return Jwts.builder()
                .setSubject(id)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    private String createAuthToken(String id, String role, Date expiry) {
        return Jwts.builder()
                .setSubject(id)
                .claim(AUTHORITIES_KEY, role)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiry)
                .compact();
    }

    public boolean validate() {
        return this.getTokenClaims() != null;
    }

    public Claims getTokenClaims() {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.info("Invalid JWT signature.");
        } catch (MalformedJwtException e) {
            log.info("Invalid JWT token.");
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT token.");
        } catch (IllegalArgumentException e) {
            log.info("JWT token compact of handler are invalid.");
        }
        return null;
    }

    public Claims getExpiredTokenClaims() {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT token.");
            return e.getClaims();
        }
        return null;
    }

}
