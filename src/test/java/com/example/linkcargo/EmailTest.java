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
    @Description("이메일 전송 테스트")
    public void 이메일_전송_테스트() {
        emailService.sendMailNotice("hyunnn0524@naver.com");
    }
}
