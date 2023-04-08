package com.catchtwobirds.soboro.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Schema(description = "회원 로그인 요청 DTO")
public class AuthReqModel {
    @Schema(description = "아이디")
    private String id;
    @Schema(description = "비밀번호")
    private String password;
}
