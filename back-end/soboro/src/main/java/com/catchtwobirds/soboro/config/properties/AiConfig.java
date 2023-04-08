package com.catchtwobirds.soboro.config.properties;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class AiConfig {
    @Value("${file.ttsDir}")
    private String ttsPrefix;
}
