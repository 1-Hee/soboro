package com.catchtwobirds.soboro.user.controller;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.catchtwobirds.soboro.common.ApiResponseDto;
import com.catchtwobirds.soboro.user.dto.UserRequestDto;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.service.UserService;
import com.sun.jdi.request.DuplicateRequestException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "user", description = "회원 관련 컨트롤러")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CustomOAuth2UserService customOAuth2UserService;

//    @PostMapping
//    public void addUser(@RequestBody UserDto userDto) {
//        System.out.println(userDto);
//        userService.insertUser(userDto);
//    }

    @GetMapping
    @Operation(summary = "회원 정보 반환", description = "회원 정보를 반환한다. (ADMIN 요청)", tags = {"user"})
    public ApiResponseDto<?> getUser() {
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        log.info("getUser principal : {}", principal);
        User user = userService.getUser(principal.getUsername());
        log.info("getUser user : {}", user);
        return ApiResponseDto.success("user", user);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {

        // ID 중복
        if(userService.getUser(userRequestDto.getUserId())!= null) {
            throw new DuplicateRequestException("Email address already in use.");
        }

        userRequestDto.setUserPassword(passwordEncoder.encode(userRequestDto.getUserPassword()));
        log.info("회원 가입 :  {}", userRequestDto);
        User result = userService.insertUser(userRequestDto);
        log.info("result : {} ", result);
        userRequestDto.setUserPassword(null);
        return ResponseEntity.status(HttpStatus.OK).body(userRequestDto);
    }

    @GetMapping("/infoooo")
    public ResponseEntity<?> getUserInfo(@RequestHeader String Authorization) {
        log.info("Authorization : {}", Authorization);
        String token = HeaderUtil.getAccessTokenString(Authorization);
        String id = customOAuth2UserService.getId(token);
        User user = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
