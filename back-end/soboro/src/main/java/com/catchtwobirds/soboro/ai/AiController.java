package com.catchtwobirds.soboro.ai;

import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.config.properties.AiConfig;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;

import java.net.MalformedURLException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/ai")
@Tag(name = "ai", description = "인공지능 관련 컨트롤러")
public class AiController {

    private final AiConfig aiConfig;
//    @GetMapping("/tts")
//    public RestApiResponse<?> ttsAddress(@RequestBody AiDto aiDto) {
//        String baseUrl = aiConfig.getTtsPrefix();
//        String address = aiDto.getAddress();
//        String URL = baseUrl + address;
//        return new RestApiResponse<>("음성파일 주소 전달", URL);
//    }

    @GetMapping("/tts")
    public RestApiResponse<?> ttsAddress(
            @RequestParam(value = "address") String address
    ) throws MalformedURLException {
//        String baseUrl = aiConfig.getTtsPrefix();
        String baseUrl = aiConfig.getTtsPrefix();
        String URL = baseUrl + address;
        UrlResource resource = new UrlResource(URL);
//        return new UrlResource(URL);
//        UrlResource urlResource = new UrlResource("https", "file");
//        return new RestApiResponse<>("음성파일 주소 전달", URL);
        return new RestApiResponse<>("음성파일 주소 전달", resource);

    }
}
