package com.example.linkcargo.domain.notification.dto;


public record NotificationDTO(
    Long id,

    Long userId,

    String title,

    String content,

    boolean isRead
) {

}
