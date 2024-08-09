package com.example.linkcargo.domain.quotation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuotationStatus {
    BASIC_INFO, // 화주 견적서 요청 상태
    DETAIL_INFO, // 포워더 견적서 작성 후 상태,
    ESTIMATE_SHEET // 확정 완료된 견적서

}
