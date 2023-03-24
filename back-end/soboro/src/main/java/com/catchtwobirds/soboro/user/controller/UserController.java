package com.catchtwobirds.soboro.user.controller;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.common.error.errorcode.UserErrorCode;
import com.catchtwobirds.soboro.common.error.exception.RestApiException;
import com.catchtwobirds.soboro.common.error.response.ErrorResponse;
import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.user.dto.UserModifyDto;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.catchtwobirds.soboro.user.dto.UserRequestDto;
import com.catchtwobirds.soboro.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping
    @Operation(summary = "회원 정보 반환", description = "회원 정보 반환 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "USER_401", description = "회원 정보 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @SecurityRequirement(name = "bearerAuth")
    public RestApiResponse<?> getUserInfo(@RequestHeader(required = false) String Authorization) {
        log.info("/api/user | GET method | 회원 정보 반환 요청됨");
        log.info("Authorization : {}", Authorization);
        String token = HeaderUtil.getAccessTokenString(Authorization);
        String id = customOAuth2UserService.getId(token);
        // DB에서 회원 정보 가져오기
        UserResponseDto result = userService.getUser(id);
        // 회원 정보가 없다면
        if(result == null) {
            throw new RestApiException(UserErrorCode.USER_401);
        }

        return new RestApiResponse<>("회원 정보 반환 완료", result);
    }

    @PostMapping
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
        log.info("/api/user | POST method | 회원 가입 요청됨");
        log.info("userRequestDto : {}", userRequestDto);
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
        return new RestApiResponse<>("회원 가입 완료", result);
    }

    @PatchMapping
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "회원 정보 수정", description = "회원 정보 수정 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserModifyDto.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> modifyUserInfo(
            @RequestHeader(required = false) String Authorization,
            @Valid @RequestBody UserModifyDto userModifyDto) {
        log.info("/api/user | PATCH method | 회원 정보 수정 요청됨");
        log.info("HeaderUtil.getAccessTokenString(Authorization) : {} ", HeaderUtil.getAccessTokenString(Authorization));
        log.info("userModifyRequestDto : {}", userModifyDto);

        String token = HeaderUtil.getAccessTokenString(Authorization);
        String id = customOAuth2UserService.getId(token);
        userModifyDto.setUserId(id);
        UserModifyDto result = userService.modifyUser(userModifyDto);

        return new RestApiResponse<>("회원 정보 수정 완료", result);
    }


    @PostMapping("/duplicate/id")
    @Operation(summary = "회원 아이디 중복 확인", description = "회원 아이디 중복 확인 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "USER_410", description = "회원 ID중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> checkUserId(@Valid @RequestParam("idCheck") String idCheck) {
        log.info("/api/user/duplicate/id | POST method | 회원 아이디 중복 확인 요청됨");
        log.info("idCheck : {}", idCheck);
    
        // 아이디 중복 체크
        Object userResponseDto = userService.getUserId(idCheck);
        log.info("userResponseDto : {}", userResponseDto);
        // 아이디 DB에 있으면 에러반환

        return new RestApiResponse<>("아이디 사용 가능");
    }

    // 미구현
    @PostMapping("/certification")
    @Operation(summary = "인증번호 확인 (미구현)", description = "인증번호 확인 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "USER_410", description = "회원 ID중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "USER_500", description = "DB저장실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> certificateNumber(@Valid @RequestParam("number") String number) {
        log.info("/api/user/certification | POST method | 인증번호 확인 요청됨");
        log.info("number : {}", number);

        return new RestApiResponse<>("아이디 사용 가능");
    }

//    @PostMapping("/sendnumber/id")
//    @Operation(summary = "인증번호 전송", description = "인증번호 전송 API", tags = {"user"})
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "OK", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
//                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    public RestApiResponse<?> certificateNumber(@Valid @RequestParam("number") String number) {
//        log.info("/api/user/sendnumber/id| POST method | 인증번호 전송 요청됨");
//        log.info("number : {}", number);
//
//        return new RestApiResponse<>("아이디 사용 가능");
//    }
//
//    @PostMapping("/find/id")
//    @Operation(summary = "회원 아이디 찾기", description = "휴대폰 번호를 통한 회원 찾기 API", tags = {"user"})
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "OK", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
//                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//            @ApiResponse(responseCode = "USER_410", description = "회원 ID중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
//    })
//    public RestApiResponse<?> findUserId(@Valid @RequestParam("address") String address) {
//        log.info("/api/user/find/id | POST method | 회원 아이디 찾기 요청됨");
//        log.info("address : {}", address);
//
//        // 아이디 중복 체크
//        Object userResponseDto = userService.getUserId(idCheck);
//        log.info("userResponseDto : {}", userResponseDto);
//        // 아이디 DB에 있으면 에러반환
//
//        return new RestApiResponse<>("아이디 사용 가능");
//    }
}
