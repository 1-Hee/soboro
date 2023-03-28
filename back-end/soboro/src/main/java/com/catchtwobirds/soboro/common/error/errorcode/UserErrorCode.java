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
    USER_410(HttpStatus.OK, "인증 번호 불일치"),
    USER_411(HttpStatus.OK, "유효하지 않은 코드"),
    USER_500(HttpStatus.OK, "DB 등록 실패"),
    USER_501(HttpStatus.OK, "DB 수정 실패"),
    USER_444(HttpStatus.OK, "토큰 에러"),

    USER_600(HttpStatus.OK, "파일 경로에 해당 파일이 존재하지않음"),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}