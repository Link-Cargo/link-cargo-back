package com.example.linkcargo.domain.notification;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    /**
     * 이메일 전송
     */
    public void sendMailNotice(String email, String title, String content, String buttonTitle, String buttonUrl) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email); // 수신자 메일
            mimeMessageHelper.setSubject(title); // 메일 제목
            mimeMessageHelper.setText(setContext(todayDate(), title, content, buttonTitle, buttonUrl), true); // 메일 본문
            javaMailSender.send(mimeMessage);

            log.info("SUCCEEDED TO SEND EMAIL to {}", email);
        } catch (Exception e) {
            log.error("FAILED TO SEND EMAIL to {}", email, e);
            throw new RuntimeException(e);
        }
    }

    public String todayDate() {
        ZonedDateTime todayDate = LocalDateTime.now(ZoneId.of("Asia/Seoul")).atZone(ZoneId.of("Asia/Seoul"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M d");
        return todayDate.format(formatter);
    }

    // Thymeleaf 통한 HTML 적용 (date, title, content, url 변수를 템플릿에 전달)
    public String setContext(String date, String title, String content, String buttonTitle, String buttonUrl) {
        Context context = new Context();
        context.setVariable("date", date);
        context.setVariable("title", title);
        context.setVariable("content", content);
        context.setVariable("buttonTitle", buttonTitle);
        context.setVariable("buttonUrl", buttonUrl);
        return templateEngine.process("email", context);
    }
}
