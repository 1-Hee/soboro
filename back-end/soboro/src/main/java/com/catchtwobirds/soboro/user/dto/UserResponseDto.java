package com.catchtwobirds.soboro.user.dto;

import com.catchtwobirds.soboro.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원 정보 반환 DTO")
public class UserResponseDto {
    @NotBlank
    @Schema(description = "회원 식별번호")
    private Integer userNo;
    @NotBlank
    @Schema(description = "회원 아이디")
    private String userId;
    @NotBlank
    @Schema(description = "회원 비밀번호")
    private String userPassword;
    @NotBlank
    @Schema(description = "회원 이름")
    private String userName;
    @NotBlank
    @Schema(description = "회원 이메일", example = "testEmail@email.com")
    private String userEmail;
    @NotBlank
    @Schema(description = "회원 휴대전화번호", example = "01012345678")
    private String userPhone;
    @NotBlank
    @Schema(description = "회원 성별", example = "M")
    private String userGender;
    @NotBlank
    @Schema(description = "회원 이용약관 동의")
    private boolean userTerms;
    private boolean userActive;
    @Builder
    public UserResponseDto(User user) {
        this.userNo = user.getUserNo();
        this.userId = user.getUserId();
        this.userPassword = user.getUserPassword();
        this.userName = user.getUserName();
        this.userEmail = user.getUserEmail();
        this.userPhone = user.getUserPhone();
        this.userGender = user.getUserGender();
        this.userTerms = user.isUserTerms();
        this.userActive = user.isUserActive();
    }
}
