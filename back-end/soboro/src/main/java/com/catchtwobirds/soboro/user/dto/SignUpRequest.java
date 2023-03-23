package com.catchtwobirds.soboro.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 가입 요청 DTO")
public class SignUpRequest {
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
}
