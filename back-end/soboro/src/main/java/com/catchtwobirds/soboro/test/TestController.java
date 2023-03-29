package com.catchtwobirds.soboro.test;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.auth.service.CustomUserDetailsService;
import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.consulting.dto.ConsultingListDto;
import com.catchtwobirds.soboro.consulting.service.ConsultingService;
import com.catchtwobirds.soboro.common.error.errorcode.UserErrorCode;
import com.catchtwobirds.soboro.common.error.exception.RestApiException;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.catchtwobirds.soboro.user.service.UserService;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@Tag(name = "test", description = "테스트 데이터 컨트롤러")
public class TestController {

    private final ConsultingService consultingService;
    private final CustomUserDetailsService customUserDetailsService;

    @GetMapping("/call")
    @Operation(summary = "서버 응답 테스트", description = "서버 응답 테스트 API", tags = {"test"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    public ResponseEntity<?> test () {
        return ResponseEntity.ok().body("server call test");
    }

    @GetMapping("/tokenheader")
    @Operation(summary = "AUTH HEADER 테스트", description = "HEADER -> body형태로 토큰, ID반환 API", tags = {"test"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "NOT_AUTHORIZATION"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @SecurityRequirement(name = "bearerAuth")
    public RestApiResponse<?> authTest (HttpServletRequest request) {
        UserResponseDto getUser = customUserDetailsService.currentLoadUserByUserId();
        return new RestApiResponse<>("AUTH HEADER 테스트 호출됨",
                "server call auth test | token : " + HeaderUtil.getAccessTokenString(request.getHeader("Authorization")) + " id : " + getUser.getUserName());
    }

    @GetMapping("/consult/list/noauth")
    @Operation(summary = "컨설팅 리스트 출력 (회원정보X)", description = "컨설팅 정보에 대한 리스트를 제공한다 page=0,1... size=10 userno=1 sort는 지우고 요청할 것", tags = {"test"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK" , content = @Content(array = @ArraySchema(schema = @Schema(implementation = ConsultingListDto.class)))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    public ResponseEntity<?> consultingAll(
            @Parameter(description = "페이지 관련") @PageableDefault(size = 10) Pageable pageable,
            @Parameter(description = "회원 식별번호") @RequestParam (name = "userno") Integer userNo
    ) {
        log.info("userNo : {}", userNo);
        Slice<ConsultingListDto> consultingList = consultingService.consultingList(userNo, pageable);

        return ResponseEntity.ok().body(consultingList);
    }

    @GetMapping("/error/user401")
    @Operation(summary = "커스텀 응답은 이렇게 반환됩니다.", description = "에러 응답 코드", tags = {"test"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK" , content = @Content(schema = @Schema(implementation = ConsultingListDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    public ResponseEntity<?> errorUser401() {
        throw new RestApiException(UserErrorCode.USER_401);
    }


    @GetMapping("/error/exception")
    @Operation(summary = "일반 응답은 이렇게 반환됩니다.", description = "에러 응답 코드", tags = {"test"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK" , content = @Content(schema = @Schema(implementation = ConsultingListDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    public ResponseEntity<?> errorException() throws Exception {

        throw new Exception();

    }

    @GetMapping("/response/single")
    @Operation(summary = "단일 응답은 이렇게 반환됩니다.", description = "객체 응답 코드", tags = {"test"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK" , content = @Content(schema = @Schema(implementation = ConsultingListDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    public ResponseEntity<?> responseSingle(HttpServletRequest request) {
        
        return ResponseEntity.ok().body(new RestApiResponse<>("응답됨", "데이터"));
    }

    @GetMapping("/response/list")
    @Operation(summary = "리스트 응답은 이렇게 반환됩니다.", description = "객체 응답 코드", tags = {"test"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK" , content = @Content( array = @ArraySchema(schema = @Schema(implementation = ConsultingListDto.class)))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    public ResponseEntity<?> responseList(HttpServletRequest request) {
        List<ConsultingListDto> consultingListDtoLis = consultingService.consultingAllList();
        return ResponseEntity.ok().body(new RestApiResponse<>("응답됨", consultingListDtoLis));

    }

}
