package com.catchtwobirds.soboro.consulting.dto;

import com.catchtwobirds.soboro.consulting.entity.Consulting;
import lombok.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsultingListDto {
    private int consultingNo;
    private String consultingVisitPlace;
    private LocalDateTime consultingVisitDate;
    private String consultingVisitClass;

//    public String DateToString(LocalDateTime consultingVisitDate) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd.");
//        String formattedDateTime = consultingVisitDate.format(formatter);
//        return formattedDateTime;
//    }

    @Builder
    public ConsultingListDto(Consulting consulting) {
        this.consultingNo = consulting.getConsultingNo();
        this.consultingVisitDate = consulting.getConsultingVisitDate();
//        this.consultingVisitDate = DateToString(consulting.getConsultingVisitDate());
        this.consultingVisitPlace = consulting.getConsultingVisitPlace();
        this.consultingVisitClass = consulting.getConsultingVisitClass();
    }
}
