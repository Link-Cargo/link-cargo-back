package com.example.linkcargo.domain.notification;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendMailNotice(String email, String title, String content, String url) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(email); // 메일 수신자
            mimeMessageHelper.setSubject(title); // 메일 제목에 title을 사용
            mimeMessageHelper.setText(setContext(todayDate(), title, content, url), true); // 메일 본문 내용
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

    // Thymeleaf를 통한 HTML 적용 (date, title, content, url 변수를 템플릿에 전달)
    public String setContext(String date, String title, String content, String url) {
        Context context = new Context();
        context.setVariable("date", date);
        context.setVariable("title", title);
        context.setVariable("content", content);
        context.setVariable("url", url);
        return templateEngine.process("todo", context);
    }
}