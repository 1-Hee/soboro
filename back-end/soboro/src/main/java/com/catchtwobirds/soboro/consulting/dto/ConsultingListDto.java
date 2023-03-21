package com.catchtwobirds.soboro.consulting.dto;

import com.catchtwobirds.soboro.consulting.entity.Consulting;

import java.time.LocalDateTime;

public class ConsultingListDto {
    private int consultingNo;
    private String consultingVisitPlace;
    private LocalDateTime consultingVisitDate;
    private String consultingVisitClass;

    // 상담내역 추가?

    // 비디오 추가?

    public ConsultingListDto(Consulting consulting) {
        this.consultingNo = consulting.getConsultingNo();
        this.consultingVisitDate = consulting.getConsultingVisitDate();
        this.consultingVisitPlace = consulting.getConsultingVisitPlace();
        this.consultingVisitClass = consulting.getConsultingVisitClass();

        // 상담내역 추가?

        // 비디오 추가?

    }
}
