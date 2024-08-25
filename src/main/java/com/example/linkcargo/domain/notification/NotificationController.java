package com.example.linkcargo.domain.notification;

import com.example.linkcargo.domain.notification.dto.NotificationsResponse;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "알림 목록 조회")
    public NotificationsResponse getNotifications(@AuthenticationPrincipal CustomUserDetail userDetail){
        NotificationsResponse notificationsResponse = notificationService.getNotifications(
            userDetail.getId());
        return notificationsResponse;
    }

    @GetMapping("/unread")
    @Operation(summary = "읽지 않은 알림 목록 조회")
    public NotificationsResponse getUnReadNotifications(@AuthenticationPrincipal CustomUserDetail userDetail){
        NotificationsResponse notificationsResponse = notificationService.getUnReadNotifications(
            userDetail.getId());
        return notificationsResponse;
    }

    @PutMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리")
    public void markNotificationAsRead(@PathVariable("notificationId") Long notificationId) {
        notificationService.markAsRead(notificationId);
    }
}
