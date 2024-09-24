package com.example.linkcargo.global.config;

import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAIConfig {

    @Value("${api-key.openai.key}")
    private String openAiApiKey;
    @Bean
    public OpenAiService openAiService() {
        return new OpenAiService(openAiApiKey);
    }
}
