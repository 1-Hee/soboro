package com.catchtwobirds.soboro.user.controller;

import com.catchtwobirds.soboro.auth.dto.AuthReqModel;
import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.auth.service.CustomUserDetailsService;
import com.catchtwobirds.soboro.common.error.errorcode.UserErrorCode;
import com.catchtwobirds.soboro.common.error.exception.RestApiException;
import com.catchtwobirds.soboro.common.error.response.ErrorResponse;
import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.user.dto.UserModifyDto;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.catchtwobirds.soboro.user.service.EmailService;
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
import javax.validation.constraints.Email;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Tag(name = "user", description = "회원 관련 컨트롤러")
public class UserController {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomUserDetailsService customUserDetailsService;
    private final EmailService emailService;

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
            @ApiResponse(responseCode = "USER_402", description = "\"DB 회원 정보 없음", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @SecurityRequirement(name = "bearerAuth")
    public RestApiResponse<?> getUserInfo() {
        log.info("/api/user | GET method | 회원 정보 반환 요청됨");

        UserResponseDto getUser = customUserDetailsService.currentLoadUserByUserId();
        return new RestApiResponse<>("회원 정보 반환 완료", getUser);
    }

    @PostMapping
    @Operation(summary = "회원 가입", description = "회원 가입 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class)),
                            @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "USER_401", description = "회원 ID중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "USER_500", description = "DB 등록 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> registerUser(@Valid @RequestBody UserRequestDto userRequestDto) {
        log.info("/api/user | POST method | 회원 가입 요청됨");
        log.info("userRequestDto : {}", userRequestDto);

        // ID 중복 확인
        UserResponseDto userResponseDto = userService.getUser(userRequestDto.getUserId());
        if (userResponseDto != null) {
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
    @Operation(summary = "회원 정보 수정", description = "회원 정보 수정 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserModifyDto.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "USER_501", description = "DB 수정 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public RestApiResponse<?> modifyUserInfo(
            @Valid @RequestBody UserModifyDto userModifyDto) {
        log.info("/api/user | PATCH method | 회원 정보 수정 요청됨");
        log.info("userModifyRequestDto : {}", userModifyDto);

        UserResponseDto getUser = customUserDetailsService.currentLoadUserByUserId();
        userModifyDto.setUserId(getUser.getUserId());
        UserModifyDto result = userService.modifyUser(userModifyDto);
        if(result == null) throw new RestApiException(UserErrorCode.USER_501);

        return new RestApiResponse<>("회원 정보 수정 완료", result);
    }

    @DeleteMapping
    @Operation(summary = "회원 정보 삭제", description = "회원 정보 삭제 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserModifyDto.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "USER_501", description = "DB 회원정보 삭제 실패", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @SecurityRequirement(name = "bearerAuth")
    public RestApiResponse<?> modifyUserInfo() {
        log.info("/api/user | PATCH method | 회원 정보 삭제 요청됨");

        UserResponseDto getUser = customUserDetailsService.currentLoadUserByUserId();
        userService.deleteUser(getUser.getUserId());
        return new RestApiResponse<>("회원 정보 삭제 완료");
    }

    @PostMapping("/duplicate/id")
    @Operation(summary = "회원 아이디 중복 확인", description = "회원 아이디 중복 확인 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "USER_401", description = "회원 ID중복", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> checkUserId(@Valid @RequestParam("idCheck") String idCheck) {
        log.info("/api/user/duplicate/id | POST method | 회원 아이디 중복 확인 요청됨");
        log.info("idCheck : {}", idCheck);
        // 아이디 중복 체크
        UserResponseDto userResponseDto = userService.getUser(idCheck);
        // 아이디 DB에 있으면 에러반환
        log.info("userResponseDto : {}", userResponseDto);
        if (userResponseDto !=null) {
            throw new RestApiException(UserErrorCode.USER_401);
        }
        return new RestApiResponse<>("아이디 사용 가능");
    }

    @PostMapping("/sendnumber/save")
    @Operation(summary = "회원가입 인증번호 메일 전송", description = "회원가입 이메일 인증번호 전송 API : 파라미터에 이메일을 입력하세요. (ex. email@naver.com)", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 인증번호 전송됨", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "USER_401", description = "이메일 중복됨", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> sendNumber(@Valid @Email @RequestParam("email") String email) throws Exception {
        // 동일한 이메일이 있는지 확인함.
        UserResponseDto getUser = userService.getUserByEmail(email);
        if(getUser != null) throw new RestApiException(UserErrorCode.USER_401);

        log.info("/api/user/sendnumber/save | POST method | 회원가입 이메일 인증번호 전송 요청됨");
        log.info("email : {}", email);
        String number = emailService.sendSimpleMessage(email);
        return new RestApiResponse<>("회원가입 이메일 인증번호 전송됨", number);
    }

    @PostMapping("/sendnumber/findid")
    @Operation(summary = "아이디 찾기 메일 전송", description = "아이디 찾기 인증번호 전송 API : 파라미터에 이메일을 입력하세요. (ex. email@naver.com)", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증번호 전송됨", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> sendNumberFindId(@Valid @Email @RequestParam("email") String email) throws Exception {
        // 동일한 이메일이 있는지 확인함.
        log.info("/api/user/sendnumber/id | POST method | 아이디 찾기 메일 전송 요청됨");
        log.info("email : {}", email);
        emailService.sendUserId(email);
        return new RestApiResponse<>("아이디 찾기 메일 전송됨");
    }

    @PostMapping("/sendnumber/findpass")
    @Operation(summary = "비밀번호 찾기 인증번호 메일 전송", description = "비밀번호 찾기 인증번호 전송 API : 파라미터에 이메일을 입력하세요. (ex. email@naver.com)", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증번호 전송됨", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> sendNumberFindPass(@Valid @Email @RequestParam("email") String email) throws Exception {
        // 동일한 이메일이 있는지 확인함.
        log.info("/api/user/sendnumber/findpass | POST method | 비밀번호 찾기 메일 전송 요청됨");
        log.info("email : {}", email);
        emailService.sendSimpleMessage(email);
        return new RestApiResponse<>("비밀번호 찾기 인증 번호 메일 전송됨");
    }


    @PostMapping("/certification")
    @Operation(summary = "회원가입, ID찾기 인증번호 확인", description = "인증번호 확인 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "USER_411", description = "인증 번호가 유효하지 않음.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> certificateNumber(@Valid @Email @RequestParam("email") String email, @Valid @RequestParam("code") String code) throws Exception{
        log.info("/api/user/certification | POST method | 인증번호 확인 요청됨");
        log.info("number : {}", code);
        emailService.checkCode(email, code);
        return new RestApiResponse<>("인증 번호 일치");
    }

    @PostMapping("/certification/findpass")
    @Operation(summary = "비밀번호 찾기 인증번호 확인", description = "인증번호 확인 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "USER_411", description = "인증 번호가 유효하지 않음.", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> certificateNumberFindPass(
            @Valid @RequestParam("id") String id,
            @Valid @Email @RequestParam("email") String email,
            @Valid @RequestParam("code") String code
    ) throws Exception{
        log.info("/api/user/certification/findpass | POST method | 비밀번호 찾기 인증번호 확인 요청됨");
        log.info("id : {}", id);
        log.info("email : {}", email);
        log.info("number : {}", code);
        emailService.checkCodeFindPass(id, email, code);
        return new RestApiResponse<>("인증 번호 일치");
    }

    @PatchMapping("/change/pass")
    @Operation(summary = "비밀번호 변경", description = "비밀번호 변경 API", tags = {"user"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = String.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> changePassword(@RequestBody AuthReqModel authReqModel) throws Exception{
        log.info("/api/user/change/pass | PATCH method | 비밀번호 변경 요청됨");
        log.info("id : {}", authReqModel.getId());
        log.info("email : {}", authReqModel.getPassword());
        authReqModel.setPassword(passwordEncoder.encode(authReqModel.getPassword()));
        userService.changePassword(authReqModel.getId(), authReqModel.getPassword());
        return new RestApiResponse<>("비밀번호 변경 완료");
    }
}
