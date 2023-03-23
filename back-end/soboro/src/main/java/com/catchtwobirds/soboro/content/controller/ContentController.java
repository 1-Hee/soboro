package com.catchtwobirds.soboro.content.controller;

import com.catchtwobirds.soboro.auth.service.CustomOAuth2UserService;
import com.catchtwobirds.soboro.content.dto.ContentDto;
import com.catchtwobirds.soboro.content.entity.Content;
import com.catchtwobirds.soboro.content.service.ContentService;
import com.catchtwobirds.soboro.user.service.UserService;
import com.catchtwobirds.soboro.utils.HeaderUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consult")
@Tag(name = "content", description = "컨설팅 컨텐츠 관련 컨트롤러")
public class ContentController {

    private final ContentService contentService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final UserService userService;

// /content/detail?consultingNo={consultingNo}
    @GetMapping("/content/detail")
    @Operation(summary = "컨설팅 컨텐츠 출력", description = "컨설팅 컨텐츠를 출력한다", tags = {"consult"})
    public ResponseEntity contentDetail(
//            @RequestHeader String Authorization,
            @RequestParam(value = "consultingNo", required = false) Integer consultingNo
    ) {
//        String token = HeaderUtil.getAccessTokenString(Authorization);
//        String id = customOAuth2UserService.getId(token);
//        Integer userNo = userService.getUser(id).getUserNo();

        List<ContentDto> contentList = contentService.contentDetailList(consultingNo);
        return ResponseEntity.ok().body(contentList);
    }


    // 테스트용입니다
    @GetMapping("/content/findall")
    @Operation(summary = "컨설팅 컨텐츠 출력", description = "컨설팅 컨텐츠를 출력한다", tags = {"consult"})
    public ResponseEntity test2() {
        List<ContentDto> contentList = contentService.allTest();
        return ResponseEntity.ok().body(contentList);
    }
}
