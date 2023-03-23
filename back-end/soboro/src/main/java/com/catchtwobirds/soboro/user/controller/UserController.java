package com.catchtwobirds.soboro.user.controller;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.common.error.errorcode.UserErrorCode;
import com.catchtwobirds.soboro.common.error.exception.RestApiException;
import com.catchtwobirds.soboro.common.error.response.ErrorResponse;
import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.catchtwobirds.soboro.user.dto.UserRequestDto;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.service.UserService;
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
            @ApiResponse(responseCode = "200", description = "OK", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class)),
                            @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "USER_410", description = "회원 ID중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "USER_500", description = "DB저장실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("/api/user/signup 회원 가입 메서드 요청됨");
        
        // ID 중복 확인
        if(userService.getUser(userRequestDto.getUserId()) != null) {
            throw new RestApiException(UserErrorCode.USER_401);
        }
        
        // 비밀번호 암호화
        userRequestDto.setUserPassword(passwordEncoder.encode(userRequestDto.getUserPassword()));
        log.info("회원 가입 :  {}", userRequestDto);
        // 회원 정보 DB 입력
        UserResponseDto result = userService.insertUser(userRequestDto);
        // DB 저장 확인
        if(result == null) {
            throw new RestApiException(UserErrorCode.USER_500);
        }
        // 반환시 password null처리
        result.setUserPassword(null);
        return new RestApiResponse<>("회원 가입 완료", result);
    }

    @GetMapping("/info")
    @Operation(summary = "회원 정보 반환", description = "회원 정보 반환 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "USER_410", description = "회원 ID중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "USER_500", description = "DB저장실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
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
