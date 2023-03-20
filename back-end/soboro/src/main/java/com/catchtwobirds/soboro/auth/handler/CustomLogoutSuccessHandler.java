package com.catchtwobirds.soboro.auth.handler;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.config.properties.AppProperties;
import com.catchtwobirds.soboro.utils.CookieUtil;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import com.catchtwobirds.soboro.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {
    private final AppProperties appProperties;
    private final RedisUtil redisUtil;
    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        log.info("로그아웃 시 onLogoutSuccess 로직 수행");
        try {
            // 헤더에서 accesstoken 가져오기
            String accesstoken = HeaderUtil.getAccessToken(request);
            log.info("Logout access_token = {}",accesstoken);

            // Redis에 엑세스 토큰 블랙리스트 등록
            redisUtil.setDataExpire(accesstoken, "true", appProperties.getAuth().getTokenExpiry());
            log.info("엑세스 토큰 블랙리스트 등록");
        }catch(Exception e){
            e.printStackTrace();
        }

        // Cookie에서 RefreshToken을 가져와 Redis에서 RefreshToken 삭제
        String refreshtoken = CookieUtil.getRefreshTokenCookie(request);

        log.info("refreshtoken 가져오기 : {}", refreshtoken);
        String id = customOAuth2UserService.getId(refreshtoken);
        log.info("Log out id = {}", id);
        redisUtil.delData(id);

        response.setStatus(HttpServletResponse.SC_OK);
        response.sendRedirect("/");
    }
}

