package com.catchtwobirds.soboro.common.error.errorcode;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_401(HttpStatus.BAD_REQUEST, "회원 아이디 중복됨"),
    USER_402(HttpStatus.BAD_REQUEST, "회원 휴대폰 미인증"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}