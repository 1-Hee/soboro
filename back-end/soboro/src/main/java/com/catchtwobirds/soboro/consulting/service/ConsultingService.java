package com.catchtwobirds.soboro.consulting.service;

import com.catchtwobirds.soboro.consulting.dto.ConsultingDetailDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingListDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingRequestDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingResponseDto;
import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.consulting.repository.ConsultingRepository;
import com.catchtwobirds.soboro.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public List<ConsultingDetailDto> consultingDetailList(Integer userNo, Integer consultingNo) {
        List<Consulting> result = consultingRepository.findConsultingDetail(userNo, consultingNo);
        return result.stream()
                .map(ConsultingDetailDto::new)
                .collect(Collectors.toList());

//        return consultingRepository.findConsultingDetailByUserIdAndConsultingNo(userNo, consultingNo);
    }

    // 컨설팅 저장하기
    @Transactional
    public ConsultingResponseDto addConsulting(ConsultingRequestDto consultingRequestDto, Integer userNo) {
//
//        System.out.println("=============================");
//        System.out.println("consultingRequestDto = " + consultingRequestDto);
//        System.out.println("=============================");

        consultingRequestDto.setUser(User.builder().userNo(userNo).build());



        return new ConsultingResponseDto(consultingRepository.saveAndFlush(consultingRequestDto.toEntity()));
    }

}
