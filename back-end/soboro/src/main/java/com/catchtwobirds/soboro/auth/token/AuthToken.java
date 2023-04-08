package com.catchtwobirds.soboro.auth.token;

import com.catchtwobirds.soboro.common.error.errorcode.CommonErrorCode;
import com.catchtwobirds.soboro.common.error.exception.RestApiException;
import com.catchtwobirds.soboro.common.error.response.ErrorResponse;
import io.jsonwebtoken.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.security.SignatureException;
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
        // 토큰이 정상이면 토큰값이 리턴되므로 null 이 아님, ture 리턴됨
        return this.getTokenClaims() != null;
    }

    /**
     * UnsupportedJwtException : 지원되지 않는 형식이거나 구성의 JWT 토큰 <br>
     * MalformedJwtException : 유효하지 않은 구성의 JWT 토큰 <br>
     * ExpiredJwtException : 만료된 JWT 토큰 <br>
     * SignatureException : 잘못된 JWT 서명 <br>
     * IllegalArgumentException : 잘못된 JWT <br>
     */
    public Claims getTokenClaims() {
        log.info("getTokenClaims 메서드 호출");
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (MalformedJwtException e) {
            log.warn("MalformedJwtException JWT token.");
            throw new MalformedJwtException("잘못된 형식의 JWT 토큰");
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token.");
            throw new ExpiredJwtException(Jwts.header(), Jwts.claims(), "JWT 토큰 만료");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT token.");
            throw new UnsupportedJwtException("지원하지 않는 방식의 JWT 토큰");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid.");
            throw new IllegalArgumentException("핸들러의 JWT 토큰 압축이 잘못됨");
        } catch (JwtException e) {
            log.warn("invalid JWT TOKEN");
            throw new JwtException("토큰이 위변조 되었음");
        } catch (SecurityException e) {
            log.warn("SecurityException");
            throw new SecurityException("Security 에러");
        }
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
