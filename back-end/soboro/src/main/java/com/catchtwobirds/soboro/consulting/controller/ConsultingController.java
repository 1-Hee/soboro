package com.catchtwobirds.soboro.consulting.controller;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.consulting.dto.ConsultingDetailDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingListDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingRequestDto;
import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.consulting.repository.ConsultingRepository;
import com.catchtwobirds.soboro.consulting.service.ConsultingService;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.service.UserService;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consulting")
@Tag(name = "consult", description = "컨설팅 내용 관련 컨트롤러")
public class ConsultingController {

    private final ConsultingService consultingService;

    private final CustomOAuth2UserService customOAuth2UserService;

    private final UserService userService;

    @GetMapping("/list")
    @Operation(summary = "컨설팅 리스트 출력", description = "컨설팅 정보에 대한 리스트를 제공한다", tags = {"consult"})
    public ResponseEntity consultingAll(@RequestHeader String Authorization) {

        String token = HeaderUtil.getAccessTokenString(Authorization);
        String id = customOAuth2UserService.getId(token);
        Integer userNo = userService.getUser(id).getUserNo();

        log.info("userNo : {}", userNo);
        List<ConsultingListDto> consultingList = consultingService.consultingList(userNo);

        return ResponseEntity.ok().body(consultingList);
    }

    //쿼리스트링으로 넘길때는 RequestParam(value="?이후에 들어갈 이름")
    @GetMapping("/detail")
    @Operation(summary = "컨설팅 디테일 출력", description = "컨설팅 상세 정보를 제공한다", tags = {"consult"})
    public ResponseEntity consultingDetail(
            @RequestHeader String Authorization,
            @RequestParam(value = "consultingNo", required = false) Integer consultingNo
        ) {
        String token = HeaderUtil.getAccessTokenString(Authorization);
        String id = customOAuth2UserService.getId(token);
        Integer userNo = userService.getUser(id).getUserNo();

        log.info("userNo : {}", userNo);
        List<ConsultingDetailDto> consultingDetailDto = consultingService.consultingDetailList(userNo, consultingNo);
        return ResponseEntity.ok().body(consultingDetailDto);
    }

    /**
     필요한 것 : 계정 정보로 저장
     입력받아서 저장 : 위치정보, 상담날짜, 상담 업종, 비디오 주소
     */
    @PostMapping("/save")
    @Operation(summary = "상담정보 저장", description = "상담 정보를 저장한다", tags = {"consult"})
    public ResponseEntity consultingSave(
            @RequestHeader String Authorization,
            @RequestBody ConsultingRequestDto consultingRequestDto
            ) {
        String token = HeaderUtil.getAccessTokenString(Authorization);
        String id = customOAuth2UserService.getId(token);
        User user = userService.getUser(id);
//        Integer userNo = userService.getUser(id).getUserNo();

        System.out.println("=============================");
        System.out.println("user = " + user);
        System.out.println("=============================");

        System.out.println("=============================");
        System.out.println("consultingRequestDto = " + consultingRequestDto);
        System.out.println("=============================");

        return ResponseEntity.ok().body(consultingService.addConsulting(consultingRequestDto, user));
    }
}
