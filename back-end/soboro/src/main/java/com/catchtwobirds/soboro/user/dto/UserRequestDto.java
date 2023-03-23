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
    @Email
    @Schema(description = "회원 이메일")
    private String userEmail;
    @NotNull
    @Schema(description = "회원 휴대폰 번호")
    private String userPhone;
    @NotNull
    @Schema(description = "회원 성별")
    private String userGender;
    @NotNull
    @Schema(description = "회원 이용 약관 동의")
    private boolean userTerms;

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
                .build();
    }
}
