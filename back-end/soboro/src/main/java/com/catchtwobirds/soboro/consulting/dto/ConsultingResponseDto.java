package com.catchtwobirds.soboro.consulting.dto;

import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ConsultingResponseDto {
//    private User user;
    private Integer consultingNo;
    private LocalDateTime consultingVisitDate;
    private Integer consultingUserNo;
    private String consultingVisitPlace;
    private String consultingVisitClass;
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
