package com.example.linkcargo.domain.prediction.dto.request;

import lombok.Builder;

@Builder
public record PredictionCreateRequest(

    Double shipVolume, // 명복 선복량
    Double shipDeliveryVolume, // 선박 인도량
    Double shipOrderVolume, // 선박 오더량
    Double idleShipVolume, // 유후 선복량
    Double exportVolumeFromChina, // 중국발 수출량
    Integer month, // 월
    Integer year // 년
) {

}
