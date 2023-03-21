package com.catchtwobirds.soboro.consulting.service;

import com.catchtwobirds.soboro.consulting.dto.ConsultingListDto;
import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.consulting.repository.ConsultingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 트랜젝션 안에서만 변경하게
public class ConsultingService {
    private final ConsultingRepository consultingRepository;

    // 컨설팅 저장

    // 컨설팅 목록 가져오기
    public List<ConsultingListDto> consultingList(Integer userNo) {
        List<Consulting> result = consultingRepository.findByUser_UserNo(userNo);
        return result.stream()
                .map(ConsultingListDto::new)
                .collect(Collectors.toList());
    }
    // 컨설팅 상세 가져오기
    public Optional<Consulting> consultingDetailList(int userNo, int consultingNo) {
        return consultingRepository.findConsultingDetailByUserIdAndConsultingNo(userNo, consultingNo);
    }

}
