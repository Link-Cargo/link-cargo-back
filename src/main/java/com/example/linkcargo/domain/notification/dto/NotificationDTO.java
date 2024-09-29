package com.example.linkcargo.domain.notification.dto;


import com.example.linkcargo.domain.notification.NotificationType;
import java.time.LocalDateTime;

public record NotificationDTO(
    Long id,

    Long userId,

    NotificationType type,

    String title,

    String content,

    String buttonTitle,

    String buttonUrl,

    LocalDateTime createdAt,
    boolean isRead
) {

}
