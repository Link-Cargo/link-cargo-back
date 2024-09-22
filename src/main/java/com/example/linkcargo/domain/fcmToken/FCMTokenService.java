package com.example.linkcargo.domain.fcmToken;

import com.example.linkcargo.domain.notification.EmailService;
import com.example.linkcargo.domain.notification.Notification;
import com.example.linkcargo.domain.notification.NotificationService;
import com.example.linkcargo.domain.notification.NotificationType;
import com.example.linkcargo.domain.user.Role;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserService;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.FCMTokenHandler;
import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
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
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@EnableAsync
public class FCMTokenService {

    private final FCMTokenRepository fcmTokenRepository;
    private final UserService userService;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Transactional
    public void save(Long userId, String token) {
        User user = userService.getUser(userId);
        fcmTokenRepository.findByUser(user)
                .ifPresentOrElse(
                        existingToken -> existingToken.update(token),
                        () -> fcmTokenRepository.save(new FCMToken(user, token))
                );
    }

    public void sendNormalNotificationToConsignor(
            Long userId,
            String title,
            String content,
            String url
    ) {
        Map<String, String> data = new HashMap<>();
        data.put("date", LocalDateTime.now().toString());
        data.put("type", NotificationType.MESSAGE.toString());
        data.put("title", title);
        data.put("content", content);
        data.put("url", url);

        sendNotificationToConsignor(userId, data);
        sendEmailToConsignor(userId, data);
    }

    @Async
    public void sendEmailToConsignor(Long userId, Map<String, String> data) {
        try {
            emailService.sendMailNotice(userService.getEmailByUserId(userId), data.get("title"), data.get("content"), data.get("url"));
        } catch (Exception e) {
            log.error("이메일 발송 실패", e);
        }
    }

    public void sendNormalNotificationToAllConsignor(
            String title,
            String content,
            String url
    ) {
        Map<String, String> data = new HashMap<>();
        data.put("date", LocalDateTime.now().toString());
        data.put("type", NotificationType.MESSAGE.toString());
        data.put("title", title);
        data.put("content", content);
        data.put("url", url);

        sendNotificationToAllConsignor(data);
        sendEmailToAllConsignor(data);
    }

    @Async
    public void sendEmailToAllConsignor(Map<String, String> data) {
        List<String> emails = userService.getAllEmailByUserRole(Role.CONSIGNOR);
        for (String email : emails) {
            emailService.sendMailNotice(email, data.get("title"), data.get("content"), data.get("url"));
        }
    }

    public void sendADNotificationToAllConsignor(
            String title,
            String content,
            String buttonTitle,
            String buttonUrl
    ) {
        Map<String, String> data = new HashMap<>();
        data.put("date", LocalDateTime.now().toString());
        data.put("type", NotificationType.AD.toString());
        data.put("title", title);
        data.put("content", content);
        data.put("buttonTitle", buttonTitle);
        data.put("buttonUrl", buttonUrl);

        sendNotificationToAllConsignor(data);
        sendEmailToAllConsignor(data);
    }

    private void sendNotificationToConsignor(Long userId, Map<String, String> data) {
        Message fcmMessage = Message.builder()
                .setToken(getTokenByUserId(userId))
                .putAllData(data)
                .build();
        log.info("fcmMessage: {}", fcmMessage);

        try {
            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            log.info("알림 전송 성공 with ID: " + response);

            NotificationType notificationType = NotificationType.valueOf(data.get("type"));
            Notification notification = new Notification(
                    userService.getUser(userId),
                    notificationType,
                    data.get("title"),
                    data.get("content"),
                    data.get("url")
            );
            notificationService.save(notification);
        } catch (Exception e) {
            log.error("알림 전송 실패", e);
        }
    }

    private void sendNotificationToAllConsignor(Map<String, String> data) {
        List<String> tokens = getTokensOfAllConsignor();
        if (tokens.isEmpty()) {
            log.warn("전송할 토큰이 없습니다.");
            return;
        }

        MulticastMessage fcmMessage = MulticastMessage.builder()
                .addAllTokens(tokens)
                .putAllData(data)
                .build();
        log.info("fcmMessage: {}", fcmMessage);

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

            log.info("알림 전송 성공 with IDs: " + String.join(", ", successfulIds));
            if (!failedIds.isEmpty()) {
                log.error("알림 전송 실패: " + String.join(", ", failedIds));
            }

            for (User consignor : getAllConsignors()) {
                Notification notification = createNotification(consignor, data);
                notificationService.save(notification);
            }
        } catch (Exception e) {
            log.error("알림 전송 중 오류 발생", e);
        }
    }

    private Notification createNotification(User consignor, Map<String, String> data) {
        String title = data.getOrDefault("title", "제목 없음");
        String content = data.getOrDefault("content", "내용 없음");
        String url = data.getOrDefault("url", "");

        if (data.get("type").equals(NotificationType.AD.toString())) {
            String buttonTitle = data.getOrDefault("buttonTitle", "확인");
            String buttonUrl = data.getOrDefault("buttonUrl", "");
            return new Notification(
                    consignor,
                    NotificationType.AD,
                    title,
                    content,
                    buttonTitle,
                    buttonUrl
            );
        } else {
            return new Notification(
                    consignor,
                    NotificationType.MESSAGE,
                    title,
                    content,
                    url
            );
        }
    }

    public String getTokenByUserId(Long userId) {
        return fcmTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new FCMTokenHandler(ErrorStatus.FCM_TOKEN_NOT_FOUND))
                .getToken();
    }

    public List<String> getTokensOfAllConsignor() {
        return fcmTokenRepository.findAllByUserRole(Role.CONSIGNOR).stream()
                .map(FCMToken::getToken)
                .toList();
    }

    private List<User> getAllConsignors() {
        return userService.findAllUsersByRole(Role.CONSIGNOR);
    }
}

