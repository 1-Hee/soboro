package com.catchtwobirds.soboro.test;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.consulting.dto.ConsultingListDto;
import com.catchtwobirds.soboro.consulting.service.ConsultingService;
import com.catchtwobirds.soboro.user.service.UserService;
import com.catchtwobirds.soboro.utils.HeaderUtil;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@Tag(name = "test", description = "테스트 데이터 컨트롤러")
public class TestController {

    private final ConsultingService consultingService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserService userService;
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
    @SecurityRequirement(name = "bearerAuth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "NOT_AUTHORIZATION"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @Operation(summary = "AUTH HEADER 테스트", description = "HEADER -> body형태로 토큰, ID반환 API", tags = {"test"})
    public ResponseEntity<?> authTest (@RequestHeader(required = false) String Authorization ) {
        log.info("HeaderUtil.getAccessTokenString(Authorization) : {} ", HeaderUtil.getAccessTokenString(Authorization));
        String token = HeaderUtil.getAccessTokenString(Authorization);
        String id = customOAuth2UserService.getId(token);
        log.info("id");
        return ResponseEntity.ok().body("server call auth test | token : " + HeaderUtil.getAccessTokenString(Authorization) + " id : " + id);
    }

//    @GetMapping("/consult/list/all/noauth")
//    @Operation(summary = "상담내역 출력 테스트", description = "모든 상담내역을 출력한다 (모든 회원정보)", tags = {"test"})
//    @ApiResponses({
//            @ApiResponse(responseCode = "200", description = "OK"),
//            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
//            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
//    })
//    public ResponseEntity<?> contentAllList() {
//        List<ConsultingListDto> consultingList = consultingService.consultingAllList();
//        return ResponseEntity.ok().body(consultingList);
//    }

    @GetMapping("/consult/list/noauth")
    @Operation(summary = "컨설팅 리스트 출력", description = "컨설팅 정보에 대한 리스트를 제공한다 page=0,1... size=10 userno=1 sort는 지우고 요청할 것", tags = {"consult"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK" , content = @Content(schema = @Schema(implementation = ConsultingListDto.class))),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    public ResponseEntity consultingAll(
            @Parameter(description = "페이지 관련") @PageableDefault(size = 10) Pageable pageable,
            @Parameter(description = "회원 식별번호") @RequestParam (name = "userno") Integer userNo
    ) {
        log.info("userNo : {}", userNo);
        Page<ConsultingListDto> consultingList = consultingService.consultingList(userNo, pageable);

        return ResponseEntity.ok().body(consultingList);
    }
}
