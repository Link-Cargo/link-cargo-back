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


//    @Test
//    @Description("EMAIL SEND TEST")
//    public void emailSendTest() {
//        String content = """
//            (광고) 김동현 화주님! 올해 수출계획은 어떻게 되시나요?
//            작년 8월에 베트남으로 화장품을 수출하셨네요! 올해 3분기 최저 운임지수를 확인해보세요.
//
//            베트남 북부지역 주요항만 3분기 최저 운임 시기 예상 시기 - 9월 초
//            현재 운임 : 146.13  → 9월 2주차 예상운임 : 120.13
//
//            2023.08.21 수출계약 포워딩 업체 정보
//            포워딩 업체: 금영글로벌(주)
//            담당자: 홍길동 대리
//            담당자 전화번호: 010-1234-5678
//            담당자 이메일: akjfalfj@guem.ac.kr
//            """;
//        String contentWithLineBreaks = content.replace("\n", "<br>");
//
//        emailService.sendMailNotice(
//                "hyunnn0524@naver.com",
//                "(광고) 김동현 화주님! 올해 수출계획은 어떻게 되시나요?",
//                contentWithLineBreaks,
//                "화물 정보 입력하러 가기",
//                "http://www.link-cargo.com"
//        );
//    }

}
