package com.example.linkcargo.domain.forwarding.dto.response;

import com.example.linkcargo.domain.forwarding.Forwarding;
import lombok.Builder;

@Builder
public record ForwardingInfoResponse(
    Long id,
    String firmName,

    String businessNumber,

    String firmLogoImageUrl,

    String mainSubject,

    String firmUrl,

    String firmAddress,

    Integer foundedYear,

    String firmCeo,

    String firmTel
) {
    public static ForwardingInfoResponse fromEntity(Forwarding forwarding) {
        return ForwardingInfoResponse.builder()
            .id(forwarding.getId())
            .firmName(forwarding.getFirmName())
            .businessNumber(forwarding.getBusinessNumber())
            .firmLogoImageUrl(forwarding.getFirmLogoImageUrl())
            .mainSubject(forwarding.getMainSubject())
            .firmUrl(forwarding.getFirmUrl())
            .firmAddress(forwarding.getFirmAddress())
            .foundedYear(forwarding.getFoundedYear())
            .firmCeo(forwarding.getFirmCeo())
            .firmTel(forwarding.getFirmTel())
            .build();
    }

}
