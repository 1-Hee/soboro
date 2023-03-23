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
    String path;
    Object data;

    @Builder
    public RestApiResponse(String message, HttpServletRequest request, Object data) {
        this.timestamp = LocalDateTime.now();
        this.status = 200;
        this.message = message;
        this.path = request.getServletPath();
        this.data = data;
    }
}
