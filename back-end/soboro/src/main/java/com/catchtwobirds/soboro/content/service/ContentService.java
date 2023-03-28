package com.catchtwobirds.soboro.content.service;

import com.catchtwobirds.soboro.content.dto.ContentDto;
import com.catchtwobirds.soboro.content.dto.ContentRequestDto;
import com.catchtwobirds.soboro.content.dto.ContentResponseDto;
import com.catchtwobirds.soboro.content.entity.Content;
import com.catchtwobirds.soboro.content.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 트랜젝션 안에서만 내용 변경 가능
public class ContentService {

    private final ContentRepository contentRepository;

    // 컨설팅 번호를 찍어서 해당 컨텐츠를 전체조회
    public Page<ContentDto> contentDetailList(Integer consultingNo, Pageable pageable) {
        Page<Content> page = contentRepository.findAllByConsultingNo(consultingNo, pageable);
        Page<ContentDto> res = page.map(ContentDto::new);
        return res;
    }

//    public List<ContentDto> contentDetailList(Integer consultingNo) {
//        List<Content> res = contentRepository.findAllByConsultingNo(consultingNo);
//        return res.stream()
//                .map(ContentDto::new)
//                .collect(Collectors.toList());
//    }

    //테스트용 전체조회
    public List<ContentDto> findAllTest() {
        List<Content> res = contentRepository.findAll();
        return res.stream()
                .map(ContentDto::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContentResponseDto addContent(ContentRequestDto contentRequestDto ) {
        System.out.println("contentRequestDto = " + contentRequestDto);
        return new ContentResponseDto(contentRepository.save(contentRequestDto.toEntity()));
    }
    // 벌크연산 테스트
    @Transactional
    public ContentResponseDto addManyContent(List<ContentRequestDto> contentRequestDtos) {
        List<Content> contents = new ArrayList<>();
        for (ContentRequestDto dto : contentRequestDtos) {
            contents.add(dto.toEntity());
        }
        System.out.println("contents = " + contents);
        contentRepository.saveAll(contents);
        return null;
//        for (ContentRequestDto dto : contentRequestDtos) {
//            System.out.println("dto = " + dto);
//            new ContentResponseDto(contentRepository.save(dto.toEntity()));
//        }
    }
}
