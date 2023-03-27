package com.catchtwobirds.soboro.consulting.dto;



import com.catchtwobirds.soboro.consulting.entity.Consulting;
import com.catchtwobirds.soboro.user.dto.UserRequestDto;
import com.catchtwobirds.soboro.user.dto.UserResponseDto;
import com.catchtwobirds.soboro.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@ToString
@Builder
@Schema(description = "상담 내역 요청 DTO")
public class ConsultingRequestDto {
    @Schema(description = "회원 정보")
    private User user;
//    private Integer consultingUserNo;
    @Schema(description = "상담 식별번호")
    private Integer consultingNo;     // 자동 생성
//    private Integer consultingUserNo;
    @Schema(description = "상담 방문 일자")
    private LocalDateTime consultingVisitDate;
    @Schema(description = "상담 방문 장소")
    private String consultingVisitPlace;
    @Schema(description = "상담 방문 장소 분류")
    private String consultingVisitClass;
    @Schema(description = "비디오 저장 위치")
    private String videoLocation;

    @Builder
    public Consulting toEntity(User user) {
//        System.out.println("디티오");
//        System.out.println("user = " + user);
        return Consulting.builder()
                .consultingNo(consultingNo)
                .user(user)
                .consultingVisitPlace(consultingVisitPlace)
                .consultingVisitClass(consultingVisitClass)
                .videoLocation(videoLocation)
                .build();
    }
}
