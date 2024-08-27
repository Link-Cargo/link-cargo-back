package com.example.linkcargo.domain.notification.dto.response;

import com.example.linkcargo.domain.notification.dto.NotificationDTO;
import java.util.List;

public record NotificationPageResponse(
    List<NotificationDTO> notifications,
    Long totalCount,
    int page,
    int per_page
) {

}
