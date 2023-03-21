package com.catchtwobirds.soboro.consulting.controller;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.consulting.dto.ConsultingDetailDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingListDto;
import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.consulting.repository.ConsultingRepository;
import com.catchtwobirds.soboro.consulting.service.ConsultingService;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.service.UserService;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
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

//    @GetMapping("detail")
//    @Operation(summary = "컨설팅 디테일 출력", description = "컨설팅 상세 정보를 제공한다", tags = {"consult"})
//    public ResponseEntity<ConsultingDetailDto> consultingDetail(@RequestHeader String Authorization, @RequestParam(value = "consultingNo", required = false) int consultingNo) {
//        // 유저 식별번호 (토큰으로 대체)
//        int userNo = 1;
//        Optional<Consulting> consulting = consultingRepository.findConsultingDetailByUserIdAndConsultingNo(userNo, consultingNo);
//        Optional<ConsultingDetailDto> res = consulting.map(c -> new ConsultingDetailDto(consulting.get()));
//        // 상담 번호
//        return ResponseEntity.ok().body(res.get());
//    }
////    쿼리스트링으로 넘길때는 RequestParam(value="?이후에 들어갈 이름")
}
