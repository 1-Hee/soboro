package com.catchtwobirds.soboro.consulting.dto;

import com.catchtwobirds.soboro.consulting.entity.Consulting;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "상담내역 리스트 DTO")
public class ConsultingListDto {
    @Schema(description = "상담내역 식별번호")
    private int consultingNo;
    @Schema(description = "상담 장소")
    private String consultingVisitPlace;
    @Schema(description = "상담 일자")
    private String consultingVisitDate;
    @Schema(description = "상담 장소 분류")
    private String consultingVisitClass;

//    private String consultingVisitDateFormat;

    public String DateToString(LocalDateTime consultingVisitDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd.");
        String formattedDateTime = consultingVisitDate.format(formatter);
        return formattedDateTime;
    }

    @Builder
    public ConsultingListDto(Consulting consulting) {
        this.consultingNo = consulting.getConsultingNo();
//        this.consultingVisitDate = consulting.getConsultingVisitDate();
//        this.consultingVisitDateFormat = DateToString(consulting.getConsultingVisitDate());
        this.consultingVisitDate = DateToString(consulting.getConsultingVisitDate());
        this.consultingVisitPlace = consulting.getConsultingVisitPlace();
        this.consultingVisitClass = consulting.getConsultingVisitClass();
    }
}
