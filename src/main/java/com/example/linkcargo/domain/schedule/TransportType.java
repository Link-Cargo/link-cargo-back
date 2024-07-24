package com.example.linkcargo.domain.schedule;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransportType {
    DIRECT, // 직항
    TRANSSHIPMENT // 환적
}
