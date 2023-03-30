package com.catchtwobirds.soboro.content.controller;

import com.catchtwobirds.soboro.common.error.response.ErrorResponse;
import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.content.dto.ContentDto;
import com.catchtwobirds.soboro.content.dto.ContentRequestDto;
import com.catchtwobirds.soboro.content.service.ContentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consult")
@Tag(name = "content", description = "컨설팅 컨텐츠 관련 컨트롤러")
public class ContentController {

    private final ContentService contentService;

    // 컨설팅 번호와 일치하는 모든 텍스트를 가져옵니다.
    @GetMapping("/content/detail")
    @Operation(summary = "컨설팅 컨텐츠 출력", description = "컨설팅 컨텐츠를 출력한다", tags = {"content"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ContentDto.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Parameter(in = ParameterIn.QUERY, description = "페이지 번호 (0..N)", name = "page", content = @Content(schema = @Schema(type = "Integer", defaultValue = "0")))
    @Parameter(in = ParameterIn.QUERY, description = "페이지 크기", name = "size", content = @Content(schema = @Schema(type = "Integer", defaultValue = "10")))
    public RestApiResponse<?> contentDetail(
            @RequestParam(value = "consultingNo", required = false) Integer consultingNo,
            @ParameterObject Pageable pageable
    ) {
        log.info("/api/consult/content/detail | GET method | 컨설팅 컨텐츠 출력 호출됨");
        Slice<ContentDto> contentList = contentService.contentDetailList(consultingNo, pageable);
        return new RestApiResponse<>("상담 컨텐츠 출력", contentList);
    }

    // 상담 텍스트를 저장
//    @PostMapping("/save/text")
//    @Operation(summary = "컨설팅 컨텐츠 저장", description = "컨설팅 컨텐츠를 저장한다", tags = {"consult"})
//    public ResponseEntity<?> contentSave(
//            @RequestBody ContentRequestDto contentRequestDto
//    ) {
//        return ResponseEntity.ok().body(contentService.addContent(contentRequestDto));
//    }

    // 상담 컨텐츠 저장
    @PostMapping("save/text")
    @Operation(summary = "컨설팅 컨텐츠 저장", description = "컨설팅 컨텐츠를 저장한다", tags = {"content"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = Integer.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> contentSaveTest(@RequestBody ContentRequestDto contentRequestDto) {
        log.info("/api/consult/save/test | POSt method | 컨설팅 컨텐츠 저장 호출됨");

        contentService.addRedisTest(contentRequestDto);
        return new RestApiResponse<>("상담 컨텐츠 전송 완료");
    }

    // 모든 상담텍스트를 조회하는 테스트용도입니다
    @GetMapping("/content/findall")
    @Operation(summary = "컨설팅 컨텐츠 조회", description = "컨설팅 컨텐츠를 조회한다", tags = {"content"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ContentDto.class))),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @io.swagger.v3.oas.annotations.media.Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    public RestApiResponse<?> findAllTest() {
        log.info("/api/consult/content/findall | GET method | 컨설팅 컨텐츠 조회 호출됨");
        List<ContentDto> contentList = contentService.findAllTest();
        return new RestApiResponse<>("상담 컨센트 조회됨", contentList);
    }
}
