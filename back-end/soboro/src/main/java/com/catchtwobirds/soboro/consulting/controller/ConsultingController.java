package com.catchtwobirds.soboro.consulting.controller;

import com.catchtwobirds.soboro.auth.service.CustomUserDetailsService;
import com.catchtwobirds.soboro.common.error.response.ErrorResponse;
import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.consulting.dto.ConsultingDetailDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingListDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingRequestDto;
import com.catchtwobirds.soboro.consulting.dto.ConsultingResponseDto;
import com.catchtwobirds.soboro.consulting.service.ConsultingService;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.catchtwobirds.soboro.user.entity.User;
import com.catchtwobirds.soboro.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consult")
@Tag(name = "consult", description = "컨설팅 내용 관련 컨트롤러")
public class ConsultingController {

    private final ConsultingService consultingService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserService userService;

    @GetMapping("/list")
    @Operation(summary = "컨설팅 리스트 출력", description = "컨설팅 정보에 대한 리스트를 제공한다", tags = {"consult"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultingListDto.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Parameter(in = ParameterIn.QUERY, description = "페이지 번호 (0..N)", name = "page", content = @Content(schema = @Schema(type = "Integer", defaultValue = "0")))
    @Parameter(in = ParameterIn.QUERY, description = "페이지 크기", name = "size", content = @Content(schema = @Schema(type = "Integer", defaultValue = "10")))
    @SecurityRequirement(name = "bearerAuth")
    public RestApiResponse<?> consultingAll(@ParameterObject Pageable pageable) {
        log.info("/api/consult/list | GET method | 컨설팅 리스트 출력 호출됨");

        Integer userNo = customUserDetailsService.currentLoadUserByUserId().getUserNo();
        log.info("userNo : {}", userNo);
        Slice<ConsultingListDto> consultingList = consultingService.consultingList(userNo, pageable);
        return new RestApiResponse<>("상담 내역 출력 완료", consultingList);
    }

    @GetMapping("/list/search/{consultingVisitClass}")
    @Operation(summary = "컨설팅 리스트 검색", description = "검색 후 컨설팅 리스트를 제공한다", tags = {"consult"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultingListDto.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @Parameter(in = ParameterIn.QUERY, description = "페이지 번호 (0..N)", name = "page", content = @Content(schema = @Schema(type = "Integer", defaultValue = "0")))
    @Parameter(in = ParameterIn.QUERY, description = "페이지 크기", name = "size", content = @Content(schema = @Schema(type = "Integer", defaultValue = "10")))
    @SecurityRequirement(name = "bearerAuth")
    //@PageableDefault(size = 10)
    public RestApiResponse<?> consultingSearch(@PathVariable String consultingVisitClass, @ParameterObject Pageable pageable) {
        log.info("/api/consult/list/search/{consultingVisitClass} | GET method | 컨설팅 리스트 검색 호출됨");

        Integer userNo = customUserDetailsService.currentLoadUserByUserId().getUserNo();
        log.info("userNo : {}", userNo);
        Slice<ConsultingListDto> consultingListPaging = consultingService.consultingListPaging(userNo, consultingVisitClass, pageable);
        return new RestApiResponse<>("상담 검색 리스트 출력", consultingListPaging);
    }

    //쿼리스트링으로 넘길때는 RequestParam(value="?이후에 들어갈 이름")
    @GetMapping("/detail")
    @Operation(summary = "컨설팅 디테일 출력", description = "컨설팅 상세 정보를 제공한다", tags = {"consult"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ConsultingDetailDto.class))),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @SecurityRequirement(name = "bearerAuth")
    public RestApiResponse<?> consultingDetail(@RequestParam(value = "consultingNo", required = false) Integer consultingNo) {
        log.info("/api/consult/detail | GET method | 컨설팅 상세 정보 출력 호출됨");

        UserResponseDto getUser = customUserDetailsService.currentLoadUserByUserId();
        Integer userNo = getUser.getUserNo();
        log.info("userNo : {}", userNo);
        List<ConsultingDetailDto> consultingDetailDto = consultingService.consultingDetailList(userNo, consultingNo);
        return new RestApiResponse<>("상담 내역 디테일 출력", consultingDetailDto);
    }

    /**
     필요한 것 : 계정 정보로 저장
     입력받아서 저장 : 위치정보, 상담날짜, 상담 업종, 비디오 주소
     */
    @PostMapping("/save")
    @Operation(summary = "상담정보 저장", description = "상담 정보를 저장한다", tags = {"consult"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultingResponseDto.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @SecurityRequirement(name = "bearerAuth")
    public RestApiResponse<?> consultingSave(@RequestBody ConsultingRequestDto consultingRequestDto) {
        log.info("/api/consult/save | POST method | 상담 정보 저장 호출됨");
        log.info("컨텐츠 리퀘스트 디티오: {}", consultingRequestDto);

        UserResponseDto getUser = customUserDetailsService.currentLoadUserByUserId();
        String id = getUser.getUserId();
        User user = userService.getUserEntity(id);

        ConsultingResponseDto consultingResponseDto = consultingService.addConsulting(consultingRequestDto, user);
        return new RestApiResponse<>("상담 테이블 저장", consultingResponseDto.getConsultingNo());
    }

    @PatchMapping("/save/final/{consultingNo}")
    @Operation(summary = "상담 테이블 수정 (비디오 경로 설정)", description = "상담 끝났을 때 비디오 경로를 수정한다.", tags = {"consult"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OK", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ConsultingResponseDto.class)),
                    @Content(mediaType = "*/*", schema = @Schema(implementation = RestApiResponse.class)) }),
            @ApiResponse(responseCode = "400", description = "BAD REQUEST", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @SecurityRequirement(name = "bearerAuth")
    public RestApiResponse<?> consultingSaveFinal(@PathVariable Integer consultingNo, @RequestBody ConsultingRequestDto consultingRequestDto) {
        log.info("/api/consult/save/final/{consultingNo} | PATCH method | 비디오 경로 설정 호출됨");

        UserResponseDto getUser = customUserDetailsService.currentLoadUserByUserId();
        String id = getUser.getUserId();
        User user = userService.getUserEntity(id);

//        System.out.println("비디오 = " + consultingRequestDto);
        ConsultingResponseDto getConsultingResponseDto = consultingService.findOne(consultingNo, consultingRequestDto);

        return new RestApiResponse<>("상담 테이블 수정", getConsultingResponseDto);
    }
}
