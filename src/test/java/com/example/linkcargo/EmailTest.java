package com.example.linkcargo;

import com.example.linkcargo.domain.notification.EmailService;
import jdk.jfr.Description;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EmailTest {

    @Autowired
    private EmailService emailService;


    @Test
    @Description("EMAIL SEND TEST")
    public void emailSendTest() {
        emailService.sendMailNotice("hyunnn0524@naver.com", "이메일 제목", "이메일 본문", "http://www.link-cargo.com");
    }
}
