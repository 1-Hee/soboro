package com.catchtwobirds.soboro.common.error.errorcode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum CommonErrorCode implements ErrorCode {
    TOKEN_900(HttpStatus.FORBIDDEN, "권한 정보가 없는 토큰"),
    TOKEN_901(HttpStatus.UNAUTHORIZED, "유효하지 않는 엑세스 토큰"),
    TOKEN_902(HttpStatus.UNAUTHORIZED, "만료된 엑세스 토큰"),
    TOKEN_903(HttpStatus.UNAUTHORIZED, "유효하지 않는 리프래쉬 토큰"),
    TOKEN_904(HttpStatus.UNAUTHORIZED, "만료된 리프래쉬 토큰"),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "잘못된 요청"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "리소스를 찾을 수 없음"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}

