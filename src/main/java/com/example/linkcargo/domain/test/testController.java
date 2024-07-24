package com.example.linkcargo.domain.test;

import com.example.linkcargo.domain.user.dto.LoginUserInfo;
import com.example.linkcargo.global.resolver.LoginInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class testController {

    @GetMapping
    public LoginUserInfo testMethod(@LoginInfo LoginUserInfo userInfo) {
        System.out.println("userInfo = " + userInfo);
        return userInfo;
    }

}
