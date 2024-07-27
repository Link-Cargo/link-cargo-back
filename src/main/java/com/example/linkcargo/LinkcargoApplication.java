package com.example.linkcargo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class LinkcargoApplication {

    public static void main(String[] args) {
        SpringApplication.run(LinkcargoApplication.class, args);
    }

}
