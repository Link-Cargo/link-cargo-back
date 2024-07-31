package com.example.linkcargo.domain.test;

import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.dto.UserResponse;
import com.example.linkcargo.global.security.CustomUserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/test")
public class testController {

    @GetMapping
    public UserResponse testMethod(@AuthenticationPrincipal CustomUserDetail userDetail) {

        log.info("userDetail.getUser(): {}", userDetail.getUser().toUserResponse());
        return userDetail.getUser().toUserResponse();
    }

}
