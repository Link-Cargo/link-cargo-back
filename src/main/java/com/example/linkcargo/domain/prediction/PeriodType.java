package com.example.linkcargo.domain.prediction;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PeriodType {
    QUARTER, // 분기
    MONTH, // 매달
    HALF_TERM // 반기
}
