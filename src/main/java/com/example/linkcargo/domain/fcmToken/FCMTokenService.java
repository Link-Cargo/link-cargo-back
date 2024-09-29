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

    /**
     * -------- 특정 유저에게 알림 전송 --------
     */
    public void notifyUser(
            Long userId,
            NotificationType notificationType,
            String title,
            String content,
            String buttonTitle,
            String buttonUrl
    ) {
        Map<String, String> data = prepareNotificationData(notificationType, title, content, buttonTitle, buttonUrl);

        // 알림 저장 및 전송
        User user = getConsignorByUserId(userId);
        saveNotification(user, data);

        sendWebNotification(userId, data); // FCM 알림 전송
        sendEmailNotification(userId, data); // 이메일 전송
    }

    /**
     * -------- 모든 유저에게 알림 전송 --------
     */
    public void notifyAllUsers(
            NotificationType notificationType,
            String title,
            String content,
            String buttonTitle,
            String buttonUrl
    ) {
        Map<String, String> data = prepareNotificationData(notificationType, title, content, buttonTitle, buttonUrl);

        // 알림 저장 및 전송
        List<User> consignors = getAllConsignors();
        saveNotificationsForAll(consignors, data);

        sendWebNotificationsToAll(consignors, data); // FCM 다중 알림 전송
        sendEmailNotificationsToAll(consignors, data); // 이메일 전송
    }

    private Map<String, String> prepareNotificationData(NotificationType type, String title, String content, String buttonTitle, String buttonUrl) {
        Map<String, String> data = new HashMap<>();
        data.put("date", LocalDateTime.now().toString());
        data.put("type", type.toString());
        data.put("title", title);
        data.put("content", content);
        data.put("buttonTitle", buttonTitle);
        data.put("buttonUrl", buttonUrl);
        return data;
    }

    /**
     * -------- 알림 저장 --------
     */
    private void saveNotification(User user, Map<String, String> data) {
        Notification notification = createNotification(user, data);
        notificationService.save(notification);
        log.info("알림 저장 완료 for userId: {}", user.getId());
    }

    private void saveNotificationsForAll(List<User> users, Map<String, String> data) {
        for (User user : users) {
            saveNotification(user, data);
        }
    }

    /**
     * -------- FCM 전송 --------
     */
    private void sendWebNotification(Long userId, Map<String, String> data) {
        Message fcmMessage = Message.builder()
                .setToken(getTokenByUserId(userId))
                .putAllData(data)
                .build();

        try {
            String response = FirebaseMessaging.getInstance().send(fcmMessage);
            log.info("FCM 알림 전송 성공 for userId: {} with ID: {}", userId, response);
        } catch (Exception e) {
            log.error("FCM 알림 전송 실패 for userId: {}", userId, e);
        }
    }

    private void sendWebNotificationsToAll(List<User> users, Map<String, String> data) {
        List<String> tokens = getTokensOfUsers(users);
        if (tokens.isEmpty()) {
            log.warn("전송할 FCM 토큰이 없습니다.");
            return;
        }

        MulticastMessage fcmMessage = MulticastMessage.builder()
                .addAllTokens(tokens)
                .putAllData(data)
                .build();

        try {
            BatchResponse response = FirebaseMessaging.getInstance().sendMulticast(fcmMessage);
            processBatchResponse(response);
        } catch (Exception e) {
            log.error("FCM 다중 알림 전송 실패", e);
        }
    }

    private void processBatchResponse(BatchResponse response) {
        List<String> successfulIds = new ArrayList<>();
        List<String> failedIds = new ArrayList<>();

        for (SendResponse sendResponse : response.getResponses()) {
            if (sendResponse.isSuccessful()) {
                successfulIds.add(sendResponse.getMessageId());
            } else {
                failedIds.add(sendResponse.getException().getMessage());
            }
        }

        log.info("FCM 알림 전송 성공 with IDs: {}", String.join(", ", successfulIds));
        if (!failedIds.isEmpty()) {
            log.error("FCM 알림 전송 실패: {}", String.join(", ", failedIds));
        }
    }

    /**
     * -------- 이메일 전송 --------
     */
    @Async
    protected void sendEmailNotification(Long userId, Map<String, String> data) {
        try {
            emailService.sendMailNotice(
                    userService.getEmailByUserId(userId),
                    data.get("title"),
                    data.get("content"),
                    data.get("buttonTitle"),
                    data.get("buttonUrl")
            );
            log.info("이메일 전송 성공 for userId: {}", userId);
        } catch (Exception e) {
            log.error("이메일 전송 실패 for userId: {}", userId, e);
        }
    }

    @Async
    protected void sendEmailNotificationsToAll(List<User> users, Map<String, String> data) {
        for (User user : users) {
            sendEmailNotification(user.getId(), data);
        }
    }

    /**
     * -------- 유틸리티 메서드 --------
     */
    private Notification createNotification(User user, Map<String, String> data) {
        return new Notification(
                user,
                NotificationType.valueOf(data.get("type")),
                data.get("title"),
                data.get("content"),
                data.get("buttonTitle"),
                data.get("buttonUrl")
        );
    }

    private String getTokenByUserId(Long userId) {
        return fcmTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new FCMTokenHandler(ErrorStatus.FCM_TOKEN_NOT_FOUND))
                .getToken();
    }

    private List<String> getTokensOfUsers(List<User> users) {
        return fcmTokenRepository.findAllByUserIn(users).stream()
                .map(FCMToken::getToken)
                .toList();
    }

    private List<User> getAllConsignors() {
        return userService.findAllUsersByRole(Role.CONSIGNOR);
    }

    private User getConsignorByUserId(Long userId) {
        return userService.getUser(userId);
    }
}
