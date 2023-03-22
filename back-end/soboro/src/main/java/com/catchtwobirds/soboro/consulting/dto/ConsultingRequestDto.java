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
//    private User user;

    private Integer consultingNo;     // 자동 생성
    private User user;
    private LocalDateTime consultingVisitDate;
    private String consultingVisitPlace;
    private String consultingVisitClass;
    private String videoLocation;

    @Builder
    public Consulting toEntity(User user) {
        return Consulting.builder()
                .consultingNo(consultingNo)
                .user(user)
                .consultingVisitDate(consultingVisitDate)
                .consultingVisitPlace(consultingVisitPlace)
                .consultingVisitClass(consultingVisitClass)
                .videoLocation(videoLocation)
                .build();
    }
}
