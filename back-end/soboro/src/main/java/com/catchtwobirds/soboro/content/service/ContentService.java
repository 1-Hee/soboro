package com.catchtwobirds.soboro.content.service;

import com.catchtwobirds.soboro.content.dto.ContentDto;
import com.catchtwobirds.soboro.content.entity.Content;
import com.catchtwobirds.soboro.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 트랜젝션 안에서만 내용 변경 가능
public class ContentService {

    private final ContentRepository contentRepository;

    // 컨텐츠 조회
//    public List<ContentDto> contentDetailList(Integer UserNo, Integer consultingNo) {
//        List<Content> res = contentRepository.findAllByConsultingNoAndUserNo(UserNo, consultingNo);
//        System.out.println("테스트입니다 = " + res);
//        return res.stream()
//                .map(ContentDto::new)
//                .collect(Collectors.toList());
//    }
    public List<ContentDto> contentDetailList(Integer consultingNo) {
        List<Content> res = contentRepository.findContentByConsultingNo(consultingNo);
        System.out.println("테스트입니다 = " + res);
        return res.stream()
                .map(ContentDto::new)
                .collect(Collectors.toList());
    }

    //테스트용 전체조회
    public List<ContentDto> test(Integer consultingNo) {
        List<Content> res = contentRepository.findAllByConsultingNo(consultingNo);
        System.out.println("나오나 보는겁니다 = " + res);
        return res.stream()
                .map(ContentDto::new)
                .collect(Collectors.toList());
    }

    //테스트용 전체조회
    public List<ContentDto> allTest() {
        List<Content> res = contentRepository.findAll();
        System.out.println("나오나 보는겁니다 = " + res);
        return res.stream()
                .map(ContentDto::new)
                .collect(Collectors.toList());
    }
}
