package com.catchtwobirds.soboro.common.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "전역 응답 반환 DTO")
public class RestApiResponse<T> {
    @Schema(description = "응답 시간")
    LocalDateTime timestamp;
//    @Schema(description = "상태 코드", example = "200")
//    int status = 200;
    @Schema(description = "상태 메세지", example = "상태메세지")
    String message;
    @Schema(description = "응답 데이터")
    T data;

    @Builder
    public RestApiResponse(String message, T data) {
        this.timestamp = LocalDateTime.now();
//        this.status = 200;
        this.message = message;
        this.data = data;
    }

    @Builder
    public RestApiResponse(String message) {
        this.timestamp = LocalDateTime.now();
//        this.status = 200;
        this.message = message;
        this.data = null;
    }
}
