package com.example.linkcargo.domain.quotation;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QuotationStatus {

    RAW_SHEET, // 화물 정보만 입력된 상태
    BASIC_INFO, // 화주 견적서 요청 상태
    DETAIL_INFO, // 포워더 견적서 작성 후 상태,
    ESTIMATE_SHEET, // 확정 완료된 견적서,
    PREDICTION_SHEET // 예상 비용만 작성된 견적서

}
