package com.catchtwobirds.soboro.common.error.errorcode;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum UserErrorCode implements ErrorCode {
    USER_400(HttpStatus.OK, "로그인 실패 : ID, PW 확인"),
    USER_401(HttpStatus.OK, "DB 회원 아이디 중복됨"),
    USER_402(HttpStatus.OK, "DB 회원 정보 없음"),
    USER_403(HttpStatus.OK, "AUTH HEADER가 필요한 요청 : HEADER가 없음"),
    USER_410(HttpStatus.OK, "회원 휴대폰 미인증"),
    USER_500(HttpStatus.OK, "DB 등록 실패"),
    USER_501(HttpStatus.OK, "DB 수정 실패"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}