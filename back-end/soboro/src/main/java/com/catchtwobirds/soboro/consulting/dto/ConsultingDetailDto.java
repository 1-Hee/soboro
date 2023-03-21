package com.catchtwobirds.soboro.consulting.dto;

import com.catchtwobirds.soboro.consulting.entity.Consulting;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConsultingDetailDto {
    private int consultingNo;
    private String consultingVisitPlace;
    private LocalDateTime consultingVisitDate;
    private String consultingVisitClass;
    private String videoLocation;

    @Builder
    public ConsultingDetailDto(Consulting consulting) {
        this.consultingNo = consulting.getConsultingNo();
        this.consultingVisitDate = consulting.getConsultingVisitDate();
        this.consultingVisitPlace = consulting.getConsultingVisitPlace();
        this.consultingVisitClass = consulting.getConsultingVisitClass();
        this.videoLocation = consulting.getVideoLocation();
    }
}
