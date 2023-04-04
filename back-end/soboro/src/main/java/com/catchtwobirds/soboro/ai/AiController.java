package com.catchtwobirds.soboro.ai;

import com.catchtwobirds.soboro.common.response.RestApiResponse;
import com.catchtwobirds.soboro.config.properties.AiConfig;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.*;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

//    @GetMapping("/tts")
//    public ResponseEntity<?> ttsAddress(
//            @RequestParam(value = "address") String address
//    ) throws MalformedURLException, FileNotFoundException {
//
//        System.out.println("address = " + baseUrl + address);
////        Resource resource = new UrlResource(baseUrl + address);
////        Resource resource = new ClassPathResource(baseUrl + address);
//        return new ResponseEntity<Resource>(new UrlResource("file:" +"//"+ baseUrl + address), HttpStatus.OK);
//    }


//    @GetMapping("/tts")
//    public ResponseEntity<?> ttsAddress(
//            @RequestParam(value = "address") String address
//    ) throws IOException, URISyntaxException {
//
//        String stringPath = baseUrl + address;
//        System.out.println("stringPath = " + stringPath);
//
//        Path filePath = Paths.get(stringPath);
//        System.out.println("filePath = " + filePath);
//
//
//        UrlResource resource = new UrlResource(filePath.toUri());
//        System.out.println("resource = " + resource);
//
//
//        InputStream inputStream = Files.newInputStream(filePath);
//        System.out.println("inputStream = " + inputStream);
//
//
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.ALL);
//
//        headers.setContentDisposition(ContentDisposition.builder("inline").filename(address).build());
//        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
//        System.out.println("inputStreamResource = " + inputStreamResource);
//
//        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
//    }

//    @GetMapping("/tts")
//    public ResponseEntity<UrlResource> getWavFile(
//            @RequestParam(value = "address") String address
//    ) throws IOException {
//        // Load the WAV file as a UrlResource
//
//        String stringPath = baseUrl + address;
//
//        Path path = Paths.get(stringPath);
//        UrlResource wavResource = new UrlResource(path.toUri());
//
//        // Send the file as a response
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
////        headers.setContentType(MediaType.ALL);
//        headers.setContentDispositionFormData("inline", address);
//        headers.setContentLength(wavResource.contentLength());
//        return new ResponseEntity<>(wavResource, headers, HttpStatus.OK);
//    }

//    @GetMapping("/tts/test")
//    public ResponseEntity<byte[]> getWavFileTest(
//            @RequestParam(value = "address") String address
//    ) throws IOException, UnsupportedAudioFileException {
//        String stringPath = baseUrl + address;
//        System.out.println("stringPath = " + stringPath);
//        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(
//                new File(stringPath));
//        AudioFormat audioFormat = audioInputStream.getFormat();
//        byte[] bytes = new byte[(int) (audioInputStream.getFrameLength() * audioFormat.getFrameSize())];
//        audioInputStream.read(bytes);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.parseMediaType("audio/wav"));
//        headers.setContentLength(bytes.length);
//        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
//    }
//
//    @GetMapping("/tts/test2")
//    public ResponseEntity<?> ttsAddress(
//            @RequestParam(value = "address") String address
//    ) throws IOException, URISyntaxException {
//
//        String stringPath = baseUrl + address;
//        System.out.println("stringPath = " + stringPath);
//
//        Path filePath = Paths.get(stringPath);
//        System.out.println("filePath = " + filePath);
//
//
//        UrlResource resource = new UrlResource(filePath.toUri());
//        System.out.println("resource = " + resource);
//
//
//        InputStream inputStream = Files.newInputStream(filePath);
//        System.out.println("inputStream = " + inputStream);
//
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//
//        headers.setContentDisposition(ContentDisposition.builder("inline").filename(address).build());
//        InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
//        System.out.println("inputStreamResource = " + inputStreamResource);
//
//        return new ResponseEntity<>(inputStreamResource, headers, HttpStatus.OK);
//    }
//
//    @GetMapping("/tts/test3")
//    public ResponseEntity<byte[]> getFile(
//            @RequestParam(value = "address") String address
//    )
//            throws IOException, UnsupportedAudioFileException {
//
//        String stringPath = baseUrl + address;
//
//        UrlResource resource = new UrlResource(stringPath);
//        AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(resource.getInputStream());
//        byte[] data = audioInputStream.readAllBytes();
//        audioInputStream.close();
//
//        return new ResponseEntity<>(data, HttpStatus.OK);
//    }
//
//    @GetMapping("/getWavFile")
//    public ResponseEntity<Resource> G(@RequestParam(value = "address") String address) {
//
//        String stringPath = baseUrl + address;
//
//        Path wavPath = Paths.get(stringPath);
//
//        ResourceLoader resourceLoader = new DefaultResourceLoader();
//        Resource wavResource = resourceLoader.getResource("file:" + wavPath.toString());
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType("audio/wav"))
//                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + wavPath.getFileName().toString() + "\"")
//                .body(wavResource);
//    }

    @GetMapping("/tts")
    public ResponseEntity<Resource> getWavFile(@RequestParam(value = "address") String address) throws FileNotFoundException {

        String stringPath = baseUrl + address;

        Path wavPath = Paths.get(stringPath);
        FileInputStream wavStream = new FileInputStream(wavPath.toFile());
        InputStreamResource wavResource = new InputStreamResource(wavStream);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("audio/wav"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + wavPath.getFileName().toString() + "\"")
                .body(wavResource);
    }


//    @GetMapping("/tts")
//    public ResponseEntity<byte[]> getFile(
//            @RequestParam(value = "address") String address
//    ) throws IOException {
//        String FILE_PATH = baseUrl + address;
//        Path path = Paths.get(FILE_PATH);
//        Path path = Paths.get(FILE_PATH.replace('/', File.separatorChar));
//
//        byte[] fileContent = Files.readAllBytes(path);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        headers.setContentDispositionFormData("attachment", path.getFileName().toString());
//
//        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
//    }

}

