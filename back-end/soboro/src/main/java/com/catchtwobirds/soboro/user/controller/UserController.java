package com.catchtwobirds.soboro.user.controller;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    // 중요한 코드 삭제하지 말것
//    @GetMapping
//    @Operation(summary = "회원 정보 반환", description = "회원 정보를 반환한다.", tags = {"user"})
//    public ApiResponseDto<?> getUser() {
//        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        log.info("getUser principal : {}", principal);
//        User user = userService.getUser(principal.getUsername());
//        log.info("getUser user : {}", user);
//        return ApiResponseDto.success("user", user);
//    }


    @PostMapping("/signup")
    @Operation(summary = "회원 가입", description = "회원 가입 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("/api/user/signup 회원 가입 메서드 요청됨");
        
        // ID 중복 확인
        if(userService.getUser(userRequestDto.getUserId())!= null) {
            throw new DuplicateRequestException("Email address already in use.");
        }
        
        // 비밀번호 암호화
        userRequestDto.setUserPassword(passwordEncoder.encode(userRequestDto.getUserPassword()));
        log.info("회원 가입 :  {}", userRequestDto);
        // 회원 정보 DB 입력
        User result = userService.insertUser(userRequestDto);
        log.info("result : {} ", result);
        // 반환시 password null처리
        userRequestDto.setUserPassword(null);
        return ResponseEntity.status(HttpStatus.OK).body(userRequestDto);
    }

    @GetMapping("/info")
    @Operation(summary = "회원 정보 반환", description = "회원 정보 반환 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<?> getUserInfo(@RequestHeader(required = false) String Authorization) {
        log.info("Authorization : {}", Authorization);
        String token = HeaderUtil.getAccessTokenString(Authorization);
        String id = customOAuth2UserService.getId(token);
        User user = userService.getUser(id);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

}
