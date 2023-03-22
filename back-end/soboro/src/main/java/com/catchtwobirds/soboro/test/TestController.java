package com.catchtwobirds.soboro.test;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.consulting.dto.ConsultingListDto;
import com.catchtwobirds.soboro.consulting.service.ConsultingService;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

//    @GetMapping("/map")
//    @Operation(summary = "XXX", description = "", tags = {"test"})
//    public ResponseEntity<?> testMap () {
//        Map<String, Object> resultMap = new HashMap<>();
//        resultMap.put("status", 200);
//        resultMap.put("message", "요청완료");
//        List<TestDto> testDtoList = new ArrayList<>();
//        for (int i = 0; i< 3; i++) {
//            testDtoList.add(new TestDto("testId", "testName", "testPW") );
//        }
//        resultMap.put("data", testDtoList);
//        return ResponseEntity.ok().body(resultMap);
//    }

    @GetMapping("/tokenheader")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "401", description = "NOT_AUTHORIZATION"),
            @ApiResponse(responseCode = "404", description = "NOT FOUND"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @Operation(summary = "로그인 테스트", description = "로그인 응답 테스트 API", tags = {"test"})
    public ResponseEntity<?> authTest (@RequestHeader String Authorization) {
        log.info("HeaderUtil.getAccessTokenString(Authorization) : {} ", HeaderUtil.getAccessTokenString(Authorization));
        String token = HeaderUtil.getAccessTokenString(Authorization);
        String id = customOAuth2UserService.getId(token);
        log.info("id");
        return ResponseEntity.ok().body("server call auth test | token : " + HeaderUtil.getAccessTokenString(Authorization) + " id : " + id);
    }

    @GetMapping("/consult/list/all/noauth")
    @Operation(summary = "상담내역 출력 테스트", description = "모든 상담내역을 출력한다 (모든 회원정보)", tags = {"test"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    public ResponseEntity<?> contentAllList() {
        List<ConsultingListDto> consultingList = consultingService.consultingAllList();
        return ResponseEntity.ok().body(consultingList);
    }

    @GetMapping("/consult/list/noauth")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK"),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST"),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR")
    })
    @Operation(summary = "회원 1번 상담내역 출력 테스트", description = "회원정보의 상담내역 리스트를 출력한다 ", tags = {"test"})
    public ResponseEntity<?> contentMemberTest(@RequestParam (name = "userno") Integer userNo) {
        List<ConsultingListDto> consultingList = consultingService.consultingList(userNo);
        return ResponseEntity.ok().body(consultingList);
    }
}
