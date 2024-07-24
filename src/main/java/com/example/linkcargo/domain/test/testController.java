package com.example.linkcargo.domain.test;

import com.example.linkcargo.global.resolver.Login;
import com.example.linkcargo.global.resolver.LoginInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/test")
public class testController {

    @GetMapping
    public LoginInfo testMethod(@Login LoginInfo loginInfo) {
        System.out.println("loginInfo = " + loginInfo);
        return loginInfo;
    }

}
