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
    private String consultingVisitDate;
    private String consultingVisitClass;

//    private String consultingVisitDateFormat;

    public String DateToString(LocalDateTime consultingVisitDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy. MM. dd.");
        String formattedDateTime = consultingVisitDate.format(formatter);
        System.out.println("formattedDateTime = " + formattedDateTime);
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
