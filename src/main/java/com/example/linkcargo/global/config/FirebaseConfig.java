package com.example.linkcargo.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class FirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        // 클래스패스에서 파일을 읽어옵니다.
        ClassPathResource serviceAccountResource = new ClassPathResource(
            "firebase-service-account");

        // 리소스를 InputStream으로 읽어옵니다.
        GoogleCredentials credentials = GoogleCredentials.fromStream(
            serviceAccountResource.getInputStream());

        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build();

        return FirebaseApp.initializeApp(options);
    }
}