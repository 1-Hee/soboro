package com.catchtwobirds.soboro.test;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
public class TestController {

    private final CustomOAuth2UserService customOAuth2UserService;
    @GetMapping
    public ResponseEntity<?> test () {
        return ResponseEntity.ok().body("server call test");
    }

    @GetMapping("/auth")
    public ResponseEntity<?> authTest (@RequestHeader String Authorization) {
        log.info("HeaderUtil.getAccessTokenString(Authorization) : {} ", HeaderUtil.getAccessTokenString(Authorization));
        String token = HeaderUtil.getAccessTokenString(Authorization);
        String id = customOAuth2UserService.getId(token);
        log.info("id");
        return ResponseEntity.ok().body("server call auth test | token : " + HeaderUtil.getAccessTokenString(Authorization) + " id : " + id);
    }
}
