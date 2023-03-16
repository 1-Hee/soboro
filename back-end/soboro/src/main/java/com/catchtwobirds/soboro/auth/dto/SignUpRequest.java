package com.catchtwobirds.soboro.auth.dto;

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
public class SignUpRequest {
    @NotBlank
    private String userId;

    @NotBlank
    private String userPassword;

    @NotBlank
    private String userName;

    @NotBlank
    @Email
    private String userEmail;

    @NotNull
    private String userPhone;


}
