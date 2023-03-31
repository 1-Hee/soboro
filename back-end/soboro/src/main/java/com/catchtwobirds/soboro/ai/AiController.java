package com.catchtwobirds.soboro.ai;

import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.config.properties.AiConfig;
import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
@Tag(name = "ai", description = "인공지능 관련 컨트롤러")
public class AiController {

//    private final AiConfig aiConfig;
//    @GetMapping("/tts")
//    public RestApiResponse<?> ttsAddress(@RequestBody AiDto aiDto) {
//        String baseUrl = aiConfig.getTtsPrefix();
//        String address = aiDto.getAddress();
//        String URL = baseUrl + address;
//        return new RestApiResponse<>("음성파일 주소 전달", URL);
//    }

//    @Value("${app.prefix.ttsPrefix}")

    final ResourceLoader resourceLoader;
    @Value("${file.ttsDir}")
    private String baseUrl;

    @GetMapping("/tts")
    public ResponseEntity<?> ttsAddress(
            @RequestParam(value = "address") String address
    ) throws MalformedURLException, FileNotFoundException {

        System.out.println("address = " + baseUrl + address);
        return new ResponseEntity<Resource>(new UrlResource("file:" + "//" + baseUrl + address), HttpStatus.OK);


//        return new ResponseEntity<Resource>(new UrlResource("file:" +"//"+ baseUrl + address), HttpStatus.OK);
    }
}

