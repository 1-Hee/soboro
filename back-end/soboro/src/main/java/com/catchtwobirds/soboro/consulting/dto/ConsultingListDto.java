package com.catchtwobirds.soboro.consulting.dto;

import com.catchtwobirds.soboro.consulting.entity.Consulting;
import lombok.*;

import java.time.LocalDateTime;

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

    @Builder
    public ConsultingListDto(Consulting consulting) {
        this.consultingNo = consulting.getConsultingNo();
        this.consultingVisitDate = consulting.getConsultingVisitDate();
        this.consultingVisitPlace = consulting.getConsultingVisitPlace();
        this.consultingVisitClass = consulting.getConsultingVisitClass();

    }
}
