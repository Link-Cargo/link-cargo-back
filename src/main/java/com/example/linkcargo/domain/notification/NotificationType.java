package com.example.linkcargo.domain.notification;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    AD,// 광고 알림
    MESSAGE, // 일반 알림
}
