package com.catchtwobirds.soboro.consulting.dto;

import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Schema(description = "상담 내역 응답 DTO")
public class ConsultingResponseDto {
//    private User user;
    @Schema(description = "상담 식별번호")
    private Integer consultingNo;
    @Schema(description = "상담 방문 일자")
    private LocalDateTime consultingVisitDate;
    @Schema(description = "상담 회원 식별번호")
    private Integer consultingUserNo;
    @Schema(description = "상담 방문 장소")
    private String consultingVisitPlace;
    @Schema(description = "상담 방문 장소 분류")
    private String consultingVisitClass;
    @Schema(description = "비디오 저장 위치")
    private String videoLocation;

    @Builder
    public ConsultingResponseDto(Consulting consulting) {
        this.consultingNo = consulting.getConsultingNo();
        this.consultingUserNo = consulting.getUser().getUserNo();
        this.consultingVisitDate = consulting.getConsultingVisitDate();
        this.consultingVisitPlace = consulting.getConsultingVisitPlace();
        this.consultingVisitClass = consulting.getConsultingVisitClass();
        this.videoLocation = consulting.getVideoLocation();
    }
}
