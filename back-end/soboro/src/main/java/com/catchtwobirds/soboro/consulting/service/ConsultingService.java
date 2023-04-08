package com.catchtwobirds.soboro.consulting.service;

import com.catchtwobirds.soboro.consulting.dto.ConsultingDetailDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingListDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingRequestDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingResponseDto;
import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.consulting.repository.ConsultingRepository;
import com.catchtwobirds.soboro.user.dto.UserRequestDto;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 트랜젝션 안에서만 변경하게
public class ConsultingService {
    private final ConsultingRepository consultingRepository;

    private final UserRepository userRepository;

    // 컨설팅 저장

    // 컨설팅 목록 가져오기
//    public List<ConsultingListDto> consultingList(Integer userNo) {
//        List<Consulting> result = consultingRepository.findByUser_UserNo(userNo);
//        return result.stream()
//                .map(ConsultingListDto::new)
//                .collect(Collectors.toList());
//    }

    // 컨설팅 리스트 페이지화
    public Slice<ConsultingListDto> consultingList(Integer userNo, Pageable pageable) {
        Slice<Consulting> page = consultingRepository.findByUser_UserNo(userNo, pageable);
        Slice<ConsultingListDto> result = page.map(ConsultingListDto::new);
        return result;
//        return result.stream()
//                .map(ConsultingListDto::new)
//                .collect(Collectors.toList());
    }

    // 컨설팅 리스트 검색 + 페이징
    public Slice<ConsultingListDto> consultingListPaging(Integer userNo, String consultingVisitClass, Pageable pageable) {
//        Page<Consulting> page = consultingRepository.findByUser_UserNoAndConsultingVisitClass(userNo, consultingVisitClass, pageable);
        Slice<Consulting> page = consultingRepository.findByUser_UserNoAndConsultingVisitClassContaining(userNo, consultingVisitClass, pageable);
        Slice<ConsultingListDto> result = page.map(ConsultingListDto::new);
        return result;
    }

    // 컨설팅 상세 가져오기
    public List<ConsultingDetailDto> consultingDetailList(Integer userNo, Integer consultingNo) {
//        List<Consulting> result = consultingRepository.findConsultingDetail(userNo, consultingNo);
        List<Consulting> result = consultingRepository.findByUser_UserNoAndConsultingNo(userNo, consultingNo);
        return result.stream()
                .map(ConsultingDetailDto::new)
                .collect(Collectors.toList());

//        return consultingRepository.findConsultingDetailByUserIdAndConsultingNo(userNo, consultingNo);
    }

    // 컨설팅 저장하기
    @Transactional
    public ConsultingResponseDto addConsulting(ConsultingRequestDto consultingRequestDto, User user) {
//        return new ConsultingResponseDto(consultingRepository.saveAndFlush(consultingRequestDto.toEntity(user)));
        return new ConsultingResponseDto(consultingRepository.save(consultingRequestDto.toEntity(user)));
    }

    @Transactional
    public ConsultingResponseDto findOne(Integer consultingNo, ConsultingRequestDto ConsultingRequestDto) {
//        System.out.println("videoLocation = " + videoLocation);
        Consulting getConsulting = consultingRepository.findById(consultingNo).get();
        getConsulting.setVideoLocation(ConsultingRequestDto.getVideoLocation());

        return new ConsultingResponseDto(getConsulting);
//        return getConsulting.setVideoLocation(videoLocation);
//        return new ConsultingResponseDto(getConsulting.setVideoLocation(videoLocation));

//        return new ConsultingResponseDto(consultingRepository.findById(consultingNo).get());
    }

    // ======*** TEST 메서드 ***======
    // 상담내역 전체 가져오기
    public List<ConsultingListDto> consultingAllList() {
        List<Consulting> result = consultingRepository.findAll();
        return result.stream()
                .map(ConsultingListDto::new)
                .collect(Collectors.toList());
    }
}
