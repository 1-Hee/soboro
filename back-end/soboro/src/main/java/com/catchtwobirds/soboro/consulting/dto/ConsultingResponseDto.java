package com.catchtwobirds.soboro.consulting.dto;

import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.user.entity.User;
import lombok.Builder;

import java.time.LocalDateTime;

public class ConsultingResponseDto {
    private User user;
    private Integer consultingNo;
    private LocalDateTime consultingVisitDate;
    private String consultingVisitPlace;
    private String consultingVisitClass;
    private String videoLocation;

    @Builder
    public ConsultingResponseDto(Consulting consulting) {
        this.consultingNo = consulting.getConsultingNo();
        this.user = consulting.getUser();
        this.consultingVisitDate = consulting.getConsultingVisitDate();
        this.consultingVisitPlace = consulting.getConsultingVisitPlace();
        this.consultingVisitClass = consulting.getConsultingVisitClass();
        this.videoLocation = consulting.getVideoLocation();
    }
}
