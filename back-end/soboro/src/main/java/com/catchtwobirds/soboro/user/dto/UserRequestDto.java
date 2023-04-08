package com.catchtwobirds.soboro.user.dto;

import com.catchtwobirds.soboro.auth.entity.ProviderType;
import com.catchtwobirds.soboro.auth.entity.RoleType;
import com.catchtwobirds.soboro.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter @Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원 가입 요청 DTO")
public class UserRequestDto {
    @Schema(description = "회원 아이디")
    private String userId;
    @Schema(description = "회원 비밀번호")
    private String userPassword;
    @NotBlank
    @Schema(description = "회원 이름", example = "testName")
    private String userName;
    @NotBlank
    @Email
    @Schema(description = "회원 이메일",  example = "testEmail@domain.com")
    private String userEmail;
    @NotNull
    @Schema(description = "회원 휴대폰 번호", example = "01012345678")
    private String userPhone;
    @Schema(description = "회원 성별", example = "M")
    private String userGender;
    @Schema(description = "회원 이용 약관 동의")
    private boolean userTerms;
    @Schema(description = "회원 활성화 여부")
    private boolean userActive;

    public User toEntity() {
        return User.builder()
                .userId(userId)
                .userPassword(userPassword)
                .userName(userName)
                .userEmail(userEmail)
                .userPhone(userPhone)
                .userGender(userGender)
                .userTerms(userTerms)
                .roleType(RoleType.USER)
                .providerType(ProviderType.LOCAL)
                .userActive(userActive)
                .build();
    }
}
