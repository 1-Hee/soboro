package com.catchtwobirds.soboro.consulting.controller;

import com.catchtwobirds.soboro.consulting.dto.ConsultingDto;
import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.consulting.repository.ConsultingRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/consulting")
@Tag(name = "consult", description = "컨설팅 내용 관련 컨트롤러")
public class ConsultingController {

    private final ConsultingRepository consultingRepository;
    @GetMapping("list")
    @Operation(summary = "컨설팅 리스트 출력", description = "컨설팅 정보에 대한 리스트를 제공한다", tags = {"consult"})
    public ResponseEntity consultingAll() {
        // 추후 토큰으로 대체함
        int userNo = 1;
        List<Consulting> consultingList = consultingRepository.findConsultingListByUserId(userNo);
        List<ConsultingDto> res = new ArrayList<>();

        for (Consulting consulting : consultingList)
            res.add(new ConsultingDto(consulting));

        return ResponseEntity.ok().body(res);
    }
//    @GetMapping("detail")
//    @Operation(summary = "컨설팅 디테일 출력", description = "컨설팅 상세 정보를 제공한다", tags = {"consult"})
//    public ResponseEntity consultingDetail() {
//        @RequestParam(value = "consultingNo") Long consultingNo;
//        Long userNo = 1L;
//        Long consultingNo = 1L;
//
//        Optional<Consulting> consultingDetailByUserIdAndConsultingNo = consultingRepository.findConsultingDetailByUserIdAndConsultingNo(userNo, consultingNo);
//        return null;
//    }
//    쿼리스트링으로 넘길때는 RequestParam(value="?이후에 들어갈 이름")
}
