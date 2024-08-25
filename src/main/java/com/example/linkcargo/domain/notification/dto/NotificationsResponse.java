package com.example.linkcargo.domain.notification.dto;

import java.util.List;

public record NotificationsResponse(
    List<NotificationDTO> notifications
) {

}
