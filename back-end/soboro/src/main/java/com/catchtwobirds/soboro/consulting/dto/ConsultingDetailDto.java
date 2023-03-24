package com.catchtwobirds.soboro.consulting.dto;

import com.catchtwobirds.soboro.consulting.entity.Consulting;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "상담 세부내역 DTO")
public class ConsultingDetailDto {
    @Schema(description = "상담 식별번호")
    private int consultingNo;
    @Schema(description = "상담 방문 장소")
    private String consultingVisitPlace;
    @Schema(description = "상담 방문 일자")
    private String consultingVisitDate;
    @Schema(description = "상담 방문 장소 분류")
    private String consultingVisitClass;
    @Schema(description = "비디오 저장 위치")
    private String videoLocation;

    public String DateToString(LocalDateTime consultingVisitDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd.");
        String formattedDateTime = consultingVisitDate.format(formatter);
        return formattedDateTime;
    }

    @Builder
    public ConsultingDetailDto(Consulting consulting) {
        this.consultingNo = consulting.getConsultingNo();
        this.consultingVisitDate = DateToString(consulting.getConsultingVisitDate());
        this.consultingVisitPlace = consulting.getConsultingVisitPlace();
        this.consultingVisitClass = consulting.getConsultingVisitClass();
        this.videoLocation = consulting.getVideoLocation();
    }
}
