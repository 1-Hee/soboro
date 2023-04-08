package com.catchtwobirds.soboro.common.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없음"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증되지 않음"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}

