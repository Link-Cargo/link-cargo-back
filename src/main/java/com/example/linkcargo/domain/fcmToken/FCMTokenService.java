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
    public void sendNotification(Long userId, String title, String content, String directionUrl) {
        User user = userService.getUser(userId);
        String token = getToken(userId);
        Map<String, String> data = new HashMap<>();
        data.put("title", title);
        data.put("content", content);
        data.put("url", directionUrl);

        // FCM에 보낼 메시지 빌드
        MulticastMessage fcmMessage = MulticastMessage.builder()
            .addToken(token) // 특정 유저 대상
            .putAllData(data) // 추가 데이터
            .build();

        log.info("fcmMessage: {}", fcmMessage);
        try {
            // 메시지 전송
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(fcmMessage);
            List<String> successfulIds = new ArrayList<>();
            List<String> failedIds = new ArrayList<>();

            for (SendResponse sendResponse : response.getResponses()) {
                // 메시지 전송 성공
                if (sendResponse.isSuccessful()) {
                    successfulIds.add(sendResponse.getMessageId());

                    // 메시지 내역 저장
                    notificationService.save(user, "title example", content, "url", false);
                }
                // 메시지 전송 실패
                else {
                    failedIds.add(sendResponse.getException().getMessage());
                }
            }

            System.out.println(
                "Successfully sent messages with IDs: " + String.join(", ", successfulIds));
            if (!failedIds.isEmpty()) {
                System.err.println("Failed to send messages: " + String.join(", ", failedIds));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getToken(Long userId) {
        FCMToken fcmToken = fcmTokenRepository.findByUserId(userId)
            .orElseThrow(() -> new FCMTokenHandler(ErrorStatus.FCM_TOKEN_NOT_FOUND));
        return fcmToken.getToken();
    }
}
