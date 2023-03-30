package com.catchtwobirds.soboro.content.service;

import com.catchtwobirds.soboro.content.dto.ContentDto;
import com.catchtwobirds.soboro.content.dto.ContentRequestDto;
import com.catchtwobirds.soboro.content.dto.ContentResponseDto;
import com.catchtwobirds.soboro.content.entity.Content;
import com.catchtwobirds.soboro.content.repository.ContentRepository;
import com.catchtwobirds.soboro.utils.RedisUtil;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 트랜젝션 안에서만 내용 변경 가능
public class ContentService {

    private final ContentRepository contentRepository;

    private final RedisTemplate<String, ContentRequestDto> contentRedisTemplate;
    String KEY = "DATA";

    // 컨설팅 번호를 찍어서 해당 컨텐츠를 전체조회
    public Slice<ContentDto> contentDetailList(Integer consultingNo, Pageable pageable) {
        Slice<Content> page = contentRepository.findAllByConsultingNo(consultingNo, pageable);
        Slice<ContentDto> res = page.map(ContentDto::new);
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

//    컨텐츠 단건 저장
//    @Transactional
//    public ContentResponseDto addContent(ContentRequestDto contentRequestDto ) {
//        System.out.println("contentRequestDto = " + contentRequestDto);
//        return new ContentResponseDto(contentRepository.save(contentRequestDto.toEntity()));
//    }

    // 벌크연산 테스트
//    컨텐츠 여러 건 저장
//    @Transactional
//    public ContentResponseDto addManyContent(List<ContentRequestDto> contentRequestDtos) {
//        List<Content> contents = new ArrayList<>();
//        for (ContentRequestDto dto : contentRequestDtos) {
//            contents.add(dto.toEntity());
//        }
//        System.out.println("contents = " + contents);
//        contentRepository.saveAll(contents);
//        return null;
//    }

    //    레디스에 컨텐츠 밀어넣기
    @Transactional
    public void addRedisTest(ContentRequestDto contentRequestDto) {
        contentRedisTemplate.opsForList().rightPush(KEY, contentRequestDto);
    }


    //    레디스에 있는 컨텐츠 몽고디비로 저장
//    @Scheduled(fixedRate = 600000) // 실사용 10분
    @Scheduled(fixedRate = 30000) // 테스트용 30초
    @Transactional
    public void saveTextMongo() {
        List<ContentRequestDto> contentRequestDto = contentRedisTemplate.opsForList().range(KEY, 0, -1);
        log.info("컨텐츠 디티오 확인");
        log.info("contentRequestDto : {} ", contentRequestDto);

//        System.out.println("contentRequestDto = " + contentRequestDto);
        List<Content> contents = new ArrayList<>();
        if (contentRequestDto.size() != 0) {
            for (ContentRequestDto dto : contentRequestDto) {
                contents.add(dto.toEntity());
            }
            contentRepository.saveAll(contents);
            contentRedisTemplate.delete(KEY);
        }
        log.info("스케쥴 작동중");
//        System.out.println("스케쥴 작동중");
    }




    // 레디스 저장 테스트
//    @Transactional
//    public ContentResponseDto addRedis(ContentRequestDto contentRequestDto) {
//        redisUtil.setData(KEY, contentRequestDto);
//        return null;
//    }

//    @Scheduled(fixedDelay = 60000)
//    @Transactional
//    public void saveMongo() {
////        List<Object> contents =
//        redisUtil.getContentData(KEY);
//        List<Content> res = new ArrayList<>();
//        for (ContentRequestDto content : contents) {
//            res.add(content.toEntity());
//        }
//        contentRepository.saveAll(contents);
//    }


//    List<Content> contents = new ArrayList<>();
//    @Transactional
////    @Scheduled(fixedDelay = 600000)
//    public ContentResponseDto addManyContentTime(ContentRequestDto contentRequestDto) {
//        contents.add(contentRequestDto.toEntity());
//        System.out.println("contents = " + contents);
//        return null;
//    }
//
//    @Transactional
//    @Scheduled(fixedDelay = 6000)
//    public void time() {
//        contentRepository.saveAll(contents);
//    }

}
