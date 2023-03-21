package com.catchtwobirds.soboro.consulting.dto;



import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.user.entity.User;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
@Builder
public class ConsultingRequestDto {
    private User user;
    private Integer consultingNo;
    private LocalDateTime consultingVisitDate;
    private String consultingVisitPlace;
    private String consultingVisitClass;
    private String videoLocation;

    @Builder
    public Consulting toEntity() {
        return Consulting.builder()
                .consultingNo(consultingNo)
                .user(this.user)
                .consultingVisitDate(consultingVisitDate)
                .consultingVisitPlace(consultingVisitPlace)
                .consultingVisitClass(consultingVisitClass)
                .videoLocation(videoLocation)
                .build();
    }
}
