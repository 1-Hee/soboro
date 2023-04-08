package com.catchtwobirds.soboro.auth.controller;

import com.catchtwobirds.soboro.auth.dto.AuthReqModel;
import com.catchtwobirds.soboro.auth.service.CustomUserDetailsService;
import com.catchtwobirds.soboro.common.error.errorcode.UserErrorCode;
import com.catchtwobirds.soboro.common.error.exception.RestApiException;
import com.catchtwobirds.soboro.common.error.response.ErrorResponse;
import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.config.properties.AppProperties;
import com.catchtwobirds.soboro.auth.entity.UserPrincipal;
import com.catchtwobirds.soboro.auth.token.AuthToken;
import com.catchtwobirds.soboro.auth.token.AuthTokenProvider;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.catchtwobirds.soboro.user.repository.UserRefreshTokenRepository;
import com.catchtwobirds.soboro.utils.CookieUtil;
import com.catchtwobirds.soboro.utils.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "auth", description = "로그인, 로그아웃 API")

public class AuthController {

    private final AppProperties appProperties;
    private final AuthTokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserRefreshTokenRepository userRefreshTokenRepository;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisUtil redisUtil;

    private final static long THREE_DAYS_MSEC = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";

    @PostMapping("/login")
    @Operation(summary = "일반 로그인", description = "일반 로그인 API", tags = {"auth"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK : 성공", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST : 잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND : 잘못된 서버 경로 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR : 서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "USER_400", description = "로그인 실패 : ID, PW 확인", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<?> login(
            HttpServletRequest request,
            HttpServletResponse response,
            @Parameter(description = "로그인 아이디, 비밀번호") @RequestBody AuthReqModel authReqModel
    ) {
        log.info("일반로그인 post 요청");
        log.info("authReqModel : {}", authReqModel);
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authReqModel.getId(),
                            authReqModel.getPassword()
                    )
            );
        }catch (BadCredentialsException | InternalAuthenticationServiceException e) {
            throw new RestApiException(UserErrorCode.USER_400);
        }

        log.info("authentication : {} ", authentication);

        String userId = authReqModel.getId();
        SecurityContextHolder.getContext().setAuthentication(authentication);

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

        if (redisUtil.getData(userId) != null) {
            log.info("refresh token exists and Remove refresh token");
            userRefreshTokenRepository.deleteById(userId);
        }

        // DB에 refresh 토큰 새로 넣기
        redisUtil.setDataExpire(userId, refreshToken.getToken(), refreshTokenExpiry);
        
        // 쿠키 만료시간 설정
        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        return ResponseEntity.ok().body(new RestApiResponse<>("로그인 완료", accessToken.getToken()));
    }

    @PostMapping("/logout")
    @Operation(summary = "일반 로그아웃", description = "일반 로그아웃 API", tags = {"auth"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK : 성공", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST : 잘못된 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "NOT FOUND : 잘못된 서버 경로 요청", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR : 서버 에러", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        UserResponseDto userResponseDto = customUserDetailsService.currentLoadUserByUserId();
        return ResponseEntity.ok().body(new RestApiResponse<>("로그아웃 완료", userResponseDto));
    }

//    @GetMapping("/refresh")
//    @Operation(summary = "Refresh token 재발급 (페기 예정)", description = "Refresh token 재발급 API", tags = {"auth"})
//    public ResponseEntity<?> refreshToken (HttpServletRequest request, HttpServletResponse response) {
//        // access token 확인
//        String accessToken = HeaderUtil.getAccessToken(request);
//        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);
////        authToken.validate();
//
////        if (!authToken.validate()) {
////            return ApiResponse.invalidAccessToken();
////        }
//
//        // expired access token 인지 확인
////        Claims claims = authToken.getExpiredTokenClaims();
//        Claims claims = authToken.getTokenClaims();
//
////        if (claims == null) {
////            return ApiResponse.notExpiredTokenYet();
////        }
//
//        String userId = claims.getSubject();
//        RoleType roleType = RoleType.of(claims.get("role", String.class));
//
//        // refresh token
//        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
//                .map(Cookie::getValue)
//                .orElse((null));
//        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);
//        authRefreshToken.validate();
//
////        if (authRefreshToken.validate()) {
////            return ApiResponse.invalidRefreshToken();
////        }
//
//        // userId refresh token 으로 DB 확인
//        String userRefreshToken = redisUtil.getData((String) userId);
//        // UserRefreshToken userRefreshToken = userRefreshTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken);
//        if (userRefreshToken == null) {
//            throw new JwtException("토큰이 유효하지 않음");
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
//            redisUtil.setDataExpire(userId, authRefreshToken.getToken(), refreshTokenExpiry);
//
//            int cookieMaxAge = (int) refreshTokenExpiry / 60;
//            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
//            CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
//        }
//
//        return ResponseEntity.ok().body(newAccessToken.getToken());
//    }
}
