package com.example.linkcargo.domain.fcmToken;

import com.example.linkcargo.domain.notification.NotificationService;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserService;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.FCMTokenHandler;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.SendResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FCMTokenService {

    private final FCMTokenRepository fcmTokenRepository;
    private final UserService userService;
    private final NotificationService notificationService;

    /**
     * FCM TOKEN 저장(업데이트)
     */
    @Transactional
    public void save(Long userId, String token) {
        User user = userService.getUser(userId);
        Optional<FCMToken> fcmToken = fcmTokenRepository.findByUser(user);
        // 기존에 FCM 토큰이 존재하지 않는 경우 -> 새로 저장
        if (fcmToken.isEmpty()) {
            fcmTokenRepository.save(new FCMToken(user, token));
            return;
        }
        // 기존에 FCM 토큰이 존재하는 경우 -> 업데이트
        fcmToken.get().update(token);
    }

    /**
     * FCMToken 을 이용한 푸시 알림 전송
     */
    public void sendNotificationToMany(
        List<String> tokens,
        String title,
        String content,
        String url
    ) {
        Map<String, String> data = new HashMap<>();
        data.put("date", LocalDateTime.now().toString());
        data.put("title", title); // ex "견적서 도착"
        data.put("content", content); // ex "금영글로벌(주) 포워딩 업체"
        data.put("url", url); // 클릭 시 이동할 URL

        // FCM에 보낼 메시지 빌드
        MulticastMessage fcmMessage = MulticastMessage.builder()
            .addAllTokens(tokens)
            .putAllData(data) // 데이터
            .build();
        log.info("fcmMessage: {}", fcmMessage);

        // 메시지 전송
        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(fcmMessage);
            List<String> successfulIds = new ArrayList<>();
            List<String> failedIds = new ArrayList<>();

            for (SendResponse sendResponse : response.getResponses()) {
                if (sendResponse.isSuccessful()) {
                    successfulIds.add(sendResponse.getMessageId());
                } else {
                    failedIds.add(sendResponse.getException().getMessage());
                }
            }

            System.out.println("알림 전송 성공 with IDs: " + String.join(", ", successfulIds));
            if (!failedIds.isEmpty()) {
                System.err.println("알림 전송 실패: " + String.join(", ", failedIds));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 유저 ID 로 토큰 조회
     */
    public String getTokenByUserId(Long userId) {
        FCMToken fcmToken = fcmTokenRepository.findByUserId(userId)
            .orElseThrow(() -> new FCMTokenHandler(ErrorStatus.FCM_TOKEN_NOT_FOUND));
        return fcmToken.getToken();
    }

    /**
     * 유저로 토큰 조회
     */
    public String getTokenByUser(User user) {
        FCMToken fcmToken = fcmTokenRepository.findByUser(user)
            .orElseThrow(() -> new FCMTokenHandler(ErrorStatus.FCM_TOKEN_NOT_FOUND));
        return fcmToken.getToken();
    }
}
