package com.catchtwobirds.soboro.content.controller;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.content.dto.ContentDto;
import com.catchtwobirds.soboro.content.dto.ContentRequestDto;
import com.catchtwobirds.soboro.content.entity.Content;
import com.catchtwobirds.soboro.content.service.ContentService;
import com.catchtwobirds.soboro.user.service.UserService;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consult")
@Tag(name = "content", description = "컨설팅 컨텐츠 관련 컨트롤러")
public class ContentController {

    private final ContentService contentService;

    // 컨설팅 번호와 일치하는 모든 텍스트를 가져옵니다.
    @GetMapping("/content/detail")
    @Operation(summary = "컨설팅 컨텐츠 출력", description = "컨설팅 컨텐츠를 출력한다", tags = {"consult"})
    public ResponseEntity<?> contentDetail(
            @RequestParam(value = "consultingNo", required = false) Integer consultingNo,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<ContentDto> contentList = contentService.contentDetailList(consultingNo, pageable);
        return ResponseEntity.ok().body(contentList);
    }

    // 상담 텍스트를 저장
    @PostMapping("/save/text")
    @Operation(summary = "컨설팅 컨텐츠 저장", description = "컨설팅 컨텐츠를 저장한다", tags = {"consult"})
    public ResponseEntity<?> contentSave(
            @RequestBody ContentRequestDto contentRequestDto
    ) {
        return ResponseEntity.ok().body(contentService.addContent(contentRequestDto));
    }

    // 모든 상담텍스트를 조회하는 테스트용도입니다
    @GetMapping("/content/findall")
    public ResponseEntity<?> findAllTest() {
        List<ContentDto> contentList = contentService.findAllTest();
        return ResponseEntity.ok().body(contentList);
    }
}
