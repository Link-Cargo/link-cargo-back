package com.example.linkcargo.domain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/test")
public class TestController {

    @GetMapping
    public void test() {
        log.info("JWT 유효성 검사 통과");
        log.info("authentication = {}", SecurityContextHolder.getContext().getAuthentication());
    }
}
