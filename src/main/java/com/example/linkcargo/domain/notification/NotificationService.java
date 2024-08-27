package com.example.linkcargo.domain.notification;

import com.example.linkcargo.domain.notification.dto.NotificationDTO;
import com.example.linkcargo.domain.notification.dto.response.NotificationPageResponse;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.NotificationHandler;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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
     * 모든 알림 조회 - 페이징
     */
    public NotificationPageResponse getNotifications(Long userId, PageRequest pageRequest) {
        Page<Notification> notifications = notificationRepository.findAllByUserId(userId, pageRequest);
        return new NotificationPageResponse(
            makeNotificationDTOList(notifications.getContent()),
            notifications.getTotalElements(),
            notifications.getNumber(),
            notifications.getNumberOfElements()
        );
    }

    /**
     * 읽지 않은 모든 알림 조회 - 페이징
     */
    public NotificationPageResponse getUnReadNotifications(Long userId, PageRequest pageRequest) {
        Page<Notification> notificationPage = notificationRepository.findAllByUserIdAndIsReadFalse(
            userId, pageRequest);
        return new NotificationPageResponse(
            makeNotificationDTOList(notificationPage.getContent()),
            notificationPage.getTotalElements(),
            notificationPage.getNumber(),
            notificationPage.getNumberOfElements()
        );
    }

    private List<NotificationDTO> makeNotificationDTOList(List<Notification> notifications) {
        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            notificationDTOS.add(notification.toNotificationDTO());
        }
        return notificationDTOS;
    }

    /**
     * 모든 알림 삭제
     */
    @Transactional
    public void deleteAllAlarms(Long userId) {
        notificationRepository.deleteAllByUserId(userId);
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

    /**
     * 모든 알림 읽음 표시
     */
    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> notifications = notificationRepository.findAllByUserId(userId);
        notifications.forEach(notification -> notification.updateRead(true));
    }

}
