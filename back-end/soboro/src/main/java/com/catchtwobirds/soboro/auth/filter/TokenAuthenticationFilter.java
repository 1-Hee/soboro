package com.catchtwobirds.soboro.auth.filter;

import com.catchtwobirds.soboro.auth.entity.RoleType;
import com.catchtwobirds.soboro.auth.token.AuthToken;
import com.catchtwobirds.soboro.auth.token.AuthTokenProvider;
import com.catchtwobirds.soboro.common.error.response.ErrorResponse;
import com.catchtwobirds.soboro.config.properties.AppProperties;
import com.catchtwobirds.soboro.utils.CookieUtil;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import com.catchtwobirds.soboro.utils.RedisUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * 이 코드는 Spring Security에서 JWT 토큰을 검증하고 인증을 수행하는 필터인 `TokenAuthenticationFilter` 클래스입니다. <br>
 * `OncePerRequestFilter`를 상속받아 Spring Security의 `FilterChain`을 구현하고 있습니다. `TokenAuthenticationFilter`는 클라이언트의 모든 요청마다 실행됩니다. <br>
 * `AuthTokenProvider`를 생성자 주입(Dependency Injection) 받아, 요청에서 전달된 JWT 토큰을 검증하고 인증정보를 SecurityContext에 저장합니다. <br>
 * `HeaderUtil.getAccessToken(request)`를 사용하여 HTTP 요청 헤더에서 JWT 토큰 값을 가져옵니다. <br>
 * `tokenProvider.convertAuthToken(tokenStr)`을 호출하여 JWT 토큰 문자열을 `AuthToken` 객체로 변환합니다. <br>
 * `token.validate()`를 호출하여 JWT 토큰의 유효성을 확인합니다. <br>
 * `tokenProvider.getAuthentication(token)`을 호출하여 JWT 토큰에서 가져온 정보를 사용하여 `Authentication` 객체를 생성합니다. <br>
 * `SecurityContextHolder.getContext().setAuthentication(authentication)`을 호출하여 인증된 *Authentication` 객체를 Spring Security의 SecurityContext에 저장합니다. <br>
 * 마지막으로, `filterChain.doFilter(request, response)`를 호출하여 요청을 다음 필터로 전달합니다. <br>
 */

@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthTokenProvider tokenProvider;
    private final RedisUtil redisUtil;
    private final AppProperties appProperties;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)  throws ServletException, IOException {
        log.info("TokenAuthenticationFilter 호출됨");

        String refreshToken = null;

        String REFRESH_TOKEN = "refresh_token";

        try {
            String tokenStr = HeaderUtil.getAccessToken(request);
            if (tokenStr != null) {
                AuthToken token = tokenProvider.convertAuthToken(tokenStr);
                log.info("TokenAuthenticationFilter token : {}", token);

                // 블랙리스트 토큰인지 먼저 확인
                if(redisUtil.getData(token.getToken()) != null) {
                    throw new JwtException("블랙리스트 토큰");
                }

//                if (token.validate()) {
                Authentication authentication = tokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
//                }
            }
            filterChain.doFilter(request, response);
        }
        // 엑세스 토큰만료되면 리프래쉬 보고 재갱신 합시다.
        catch (ExpiredJwtException e) {
            log.info("엑세스 토큰 만료 리프래쉬 토큰 가져오기");
            e.printStackTrace();
            // refresh token 가져오기
            refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                    .map(Cookie::getValue)
                    .orElse((null));

            if (refreshToken == null) {
                throw new JwtException("리프래쉬 토큰이 없습니다.");
            }

            log.info("refreshToken : {}", refreshToken);
            AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);
            log.info("authRefreshToken : {}", authRefreshToken );

            // 유효성 검증
//            authRefreshToken.validate();
//            Authentication authentication = tokenProvider.getAuthentication(authRefreshToken);
            Claims claims = tokenProvider.getClaims(authRefreshToken);
            log.info("claims : {}", claims);

            if (claims == null) {
                throw new JwtException("토큰 만료되지 않음");
            }
            String userId = claims.getSubject();
            log.info("userId : {}", userId);
            RoleType roleType = RoleType.of(claims.get("role", String.class));
            log.info("roleType : {}", roleType);

            // userId refresh token 으로 DB 확인, 없으면 만료되었거나 존재하지 않음
            String userRefreshToken = redisUtil.getData(userId);
            log.info("userRefreshToken : {} ", userRefreshToken);
            if (userRefreshToken == null) {
                CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
                throw new JwtException("토큰 만료");
            }

//            String getUserRefreshToken = redisUtil.getData(userId);
//            if (getUserRefreshToken == null) {
//                throw new JwtException("토큰이 DB 에 존재하지 않음");
//            }

            Date now = new Date();
            AuthToken newAccessToken = tokenProvider.createAuthToken(
                    userId,
                    roleType.getCode(),
                    new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
            );

            Authentication newAuthentication = tokenProvider.getAuthentication(newAccessToken);
            log.info("TokenAuthenticationFilter new Accesstoken : {}", newAccessToken.getToken());
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Authorization", "Bearer " + newAccessToken.getToken());

            long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

            // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
            if (validTime <= 1000L * 60L * 60L * 24L * 3L) {
                // refresh 토큰 설정
                long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

                authRefreshToken = tokenProvider.createAuthToken(
                        userId,
                        roleType.getCode(),
                        new Date(now.getTime() + refreshTokenExpiry)
                );

                // DB에 refresh 토큰 업데이트
                // userRefreshToken.setRefreshToken(authRefreshToken.getToken());
                redisUtil.setDataExpire(userId, authRefreshToken.getToken(), refreshTokenExpiry);

                int cookieMaxAge = (int) refreshTokenExpiry / 60;
                CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
                CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
            }

        }

        catch (Exception e) {
            // 예외 발생하면 바로 setErrorResponse 호출
            e.printStackTrace();
            setErrorResponse(request, response, e);
        }
    }

    public void setErrorResponse(HttpServletRequest req, HttpServletResponse res, Throwable ex) throws IOException {
        log.info("JwtExceptionFilter 에서 에러 받기");
        res.setContentType(MediaType.APPLICATION_JSON_VALUE);

//        ErrorResponse result = new ErrorResponse(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", ex.getMessage());
        ErrorResponse result = new ErrorResponse("Unauthorized", ex.getMessage());
        log.info("JwtExceptionFilter ErrorResponse : {}", result);
        final ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.writeValue(res.getOutputStream(), result);
        res.setStatus(HttpServletResponse.SC_OK);
    }


}
