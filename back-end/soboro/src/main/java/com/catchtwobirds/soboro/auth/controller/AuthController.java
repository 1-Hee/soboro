package com.catchtwobirds.soboro.auth.controller;

import com.catchtwobirds.soboro.auth.dto.AuthReqModel;
import com.catchtwobirds.soboro.common.ApiResponseDto;
import com.catchtwobirds.soboro.config.properties.AppProperties;
import com.catchtwobirds.soboro.auth.entity.UserPrincipal;
import com.catchtwobirds.soboro.auth.token.AuthToken;
import com.catchtwobirds.soboro.auth.token.AuthTokenProvider;
import com.catchtwobirds.soboro.user.repository.UserRefreshTokenRepository;
import com.catchtwobirds.soboro.utils.CookieUtil;
import com.catchtwobirds.soboro.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Auth", description = "로그인, 로그아웃 API")

public class AuthController {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final RedisUtil redisUtil;

    private final static long THREE_DAYS_MSEC = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";

    @PostMapping("/login")
    @Operation(summary = "일반 로그인", description = "일반 로그인 API", tags = {"Auth"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    public ApiResponseDto<?> login(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "로그인 아이디, 비밀번호") @RequestBody AuthReqModel authReqModel
    ) {
        log.info("일반로그인 post 요청");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authReqModel.getId(),
                        authReqModel.getPassword()
                )
        );

        log.info("authentication : {} ", authentication);

        String userId = authReqModel.getId();
        SecurityContextHolder.getContext().setAuthentication(authentication);
        log.info("login 1");
        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                userId,
                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        log.info("ac token : {} ", accessToken);

        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        AuthToken refreshToken = tokenProvider.createAuthToken(
                userId,
                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        log.info("rf token : {} ", refreshToken);
        
         // 기존 refresh 토큰 삭제하기
        redisUtil.delData(userId);
        // DB에 refresh 토큰 새로 넣기
        redisUtil.setDataExpire((String) userId, refreshToken.getToken(), refreshTokenExpiry);
        
        // 쿠키 만료시간 설정
        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        return ApiResponseDto.success("token", accessToken.getToken());
    }

//    @GetMapping("/refresh")
//    public ApiResponse refreshToken (HttpServletRequest request, HttpServletResponse response) {
//        // access token 확인
//        String accessToken = HeaderUtil.getAccessToken(request);
//        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
//        if (!authToken.validate()) {
//            return ApiResponse.invalidAccessToken();
//        }
//
//        // expired access token 인지 확인
//        Claims claims = authToken.getExpiredTokenClaims();
//        if (claims == null) {
//            return ApiResponse.notExpiredTokenYet();
//        }
//
//        String userId = claims.getSubject();
//        RoleType roleType = RoleType.of(claims.get("role", String.class));
//
//        // refresh token
//        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
//                .map(Cookie::getValue)
//                .orElse((null));
//        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);
//
//        if (authRefreshToken.validate()) {
//            return ApiResponse.invalidRefreshToken();
//        }
//
//        // userId refresh token 으로 DB 확인
//        String userRefreshToken = redisUtil.getData((String) userId);
//        // UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken);
//        if (userRefreshToken == null) {
//            return ApiResponse.invalidRefreshToken();
//        }
//
//        Date now = new Date();
//        AuthToken newAccessToken = tokenProvider.createAuthToken(
//                userId,
//                roleType.getCode(),
//                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
//        );
//
//        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();
//
//        // refresh 토큰 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
//        if (validTime <= THREE_DAYS_MSEC) {
//            // refresh 토큰 설정
//            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
//
//            authRefreshToken = tokenProvider.createAuthToken(
//                    userId,
//                    roleType.getCode(),
//                    new Date(now.getTime() + refreshTokenExpiry)
//            );
//
//            // DB에 refresh 토큰 업데이트
//            // userRefreshToken.setRefreshToken(authRefreshToken.getToken());
//            redisUtil.setDataExpire((String) userId, authRefreshToken.getToken(), refreshTokenExpiry);
//
//            int cookieMaxAge = (int) refreshTokenExpiry / 60;
//            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
//            CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
//        }
//
//        return ApiResponse.success("token", newAccessToken.getToken());
//    }

}
