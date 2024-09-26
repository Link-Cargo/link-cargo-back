package com.example.linkcargo.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    MESSAGE, // 메시지 도착 알림
    QUOTATION, // 견적서 도착 알림
    AD // 광고 알림
}
