package com.example.linkcargo.domain.notification.dto;


import com.example.linkcargo.domain.notification.NotificationType;

public record NotificationDTO(
    Long id,

    Long userId,

    NotificationType type,
    String title,

    String content,

    String url,

    String buttonTitle,

    boolean isRead
) {

}
