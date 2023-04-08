package com.catchtwobirds.soboro.common.error.exception;

import com.catchtwobirds.soboro.common.error.errorcode.ErrorCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Getter

public class RestApiException extends RuntimeException {
    private ErrorCode errorCode;
    public RestApiException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }


}