package com.example.linkcargo.domain.cargo.dto;

import com.example.linkcargo.domain.port.Port;
import java.math.BigDecimal;

/**
 * Cargo 가 Jpa 를 사용하지 않기 때문에, 아래 변환과정을 거친 DTO 를 반환한다.
 * Long exportPortId -> Port exportPort
 * Long importPortId -> Port importPort
 */
public record CargoDTO(
    Port exportPort,
    Port importPort,
    String additionalInstructions,
    String friendlyDescription,
    Boolean insuranceRequired,
    CargoInfoDto cargoInfo
) {

    public record CargoInfoDto(
        String productName,
        String hsCode,
        String incoterms,
        BigDecimal weight,
        BigDecimal value,
        Integer quantity,
        BoxSizeDto boxSize
    ) {
    }

    public record BoxSizeDto(
        BigDecimal width,
        BigDecimal height,
        BigDecimal depth
    ) {
    }
}