package com.example.linkcargo.domain.notification.dto.response;

import com.example.linkcargo.domain.notification.dto.NotificationDTO;
import java.util.List;

public record NotificationsResponse(
    List<NotificationDTO> notifications
) {

}
