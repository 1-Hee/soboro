package com.catchtwobirds.soboro.common.response;

import lombok.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestApiResponse {

    LocalDateTime timestamp;
    int status;
    String message;
    Object data;

    @Builder
    public RestApiResponse(String message, Object data) {
        this.timestamp = LocalDateTime.now();
        this.status = 200;
        this.message = message;
        this.data = data;
    }
}
