package com.catchtwobirds.soboro.common.error.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@RequiredArgsConstructor
@ToString
@Schema(description = "전역 에러 반환 DTO")
public class ErrorResponse {

    @Schema(description = "응답시간")
    private final LocalDateTime timestamp = LocalDateTime.now();
//    @Schema(description = "상태코드")
//    private final int status;
    @Schema(description = "에러코드")
    private final String error; 
    @Schema(description = "상태메세지")
    private final String message;

//    @JsonInclude(JsonInclude.Include.NON_EMPTY)
//    private final List<ValidationError> errors;

//    @Getter
//    @Builder
//    @RequiredArgsConstructor
//    public static class ValidationError {
//
//        private final String field;
//        private final String message;
//
//        public static ValidationError of(final FieldError fieldError) {
//            return ValidationError.builder()
//                    .field(fieldError.getField())
//                    .message(fieldError.getDefaultMessage())
//                    .build();
//        }
//    }
}