package com.example.linkcargo.domain.notification;

import com.example.linkcargo.domain.notification.dto.NotificationDTO;
import com.example.linkcargo.domain.notification.dto.NotificationsResponse;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.NotificationHandler;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 알림 저장 - URL 지정하지 않은 경우
     */
    public void save(User user, String title, String content, boolean isRead) {
        notificationRepository.save(new Notification(user, title, content, isRead));
    }
    /**
     * 알림 저장 - URL 지정한 경우
     */
    public void save(User user, String title, String content, String url, boolean isRead) {
        notificationRepository.save(new Notification(user, title, content, url, isRead));
    }

    /**
     * 모든 알림 조회
     */
    public NotificationsResponse getNotifications(Long userId) {
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        List<Notification> notifications = notificationRepository.findAllByUserId(userId);
        for (Notification notification : notifications) {
            notificationDTOS.add(notification.toNotificationDTO());
        }
        return new NotificationsResponse(notificationDTOS);
    }

    /**
     * 읽지 않은 모든 알림 조회
     */
    public NotificationsResponse getUnReadNotifications(Long userId) {
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        List<Notification> notifications = notificationRepository.findAllByUserIdAndIsReadFalse(userId);
        for (Notification notification : notifications) {
            notificationDTOS.add(notification.toNotificationDTO());
        }
        return new NotificationsResponse(notificationDTOS);
    }

    /**
     * 해당 알림 읽음 표시
     */
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new NotificationHandler(ErrorStatus.NOTIFICATION_NOT_FOUND));
        notification.updateRead(true);
    }
}
