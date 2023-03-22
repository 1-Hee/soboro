package com.catchtwobirds.soboro.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 로그인 요청 DTO")
public class AuthReqModel {
    @Schema(description = "아이디")
    private String id;
    @Schema(description = "비밀번호")
    private String password;
}
