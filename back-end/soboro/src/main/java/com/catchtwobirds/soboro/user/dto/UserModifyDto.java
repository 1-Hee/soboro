package com.catchtwobirds.soboro.user.dto;

import com.catchtwobirds.soboro.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원 수정 요청 DTO")
public class UserModifyDto {
    @Schema(description = "회원 아이디")
    private String userId;
    @Schema(description = "회원 이름", example = "testName")
    private String userName;
    @Email
    @Schema(description = "회원 이메일",  example = "testEmail@domain.com")
    private String userEmail;
    @Schema(description = "회원 휴대폰 번호", example = "01012345678")
    private String userPhone;
    @Schema(description = "회원 활성화 여부")
    private boolean userActive;

    @Builder
    public UserModifyDto(User user){
        this.userId = user.getUserId();
        this.userName = user.getUserName();
        this.userEmail = user.getUserEmail();
        this.userPhone = user.getUserPhone();
        this.userActive = true;
    }

    @Builder
    public User toEntity() {
        return User.builder()
                .userId(userId)
                .userName(userName)
                .userEmail(userEmail)
                .userPhone(userPhone)
                .userActive(true)
                .build();
    }
}
