package com.catchtwobirds.soboro.ai;

import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.config.properties.AiConfig;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/ai")
@Tag(name = "ai", description = "인공지능 관련 컨트롤러")
public class AiController {

    private final AiConfig aiConfig;
    @GetMapping("/tts")
    public RestApiResponse<?> ttsAddress(@RequestBody AiDto aiDto) {
        String baseUrl = aiConfig.getTtsPrefix();
        String address = aiDto.getAddress();
        String URL = baseUrl + address;
        return new RestApiResponse<>("음성파일 주소 전달", URL);
    }
}
