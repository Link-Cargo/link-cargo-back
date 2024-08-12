package com.example.linkcargo.domain.cargo.dto;

import com.example.linkcargo.domain.port.Port;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Cargo 가 Jpa 를 사용하지 않기 때문에, 아래 변환과정을 거친 DTO 를 반환한다. Long exportPortId -> Port exportPort Long
 * importPortId -> Port importPort
 */
public record CargoDTO(
    String id,
    Long userId,
    Port exportPort,
    Port importPort,
    LocalDateTime wishExportDate,
    String incoterms,
    CargoInfoDto cargoInfo
) {

    public record CargoInfoDto(
        String productName,
        String hsCode,
        Integer totalQuantity,
        Integer quantityPerBox,
        BoxSizeDto boxSize,
        BigDecimal weight,
        BigDecimal value
    ) {

    }

    public record BoxSizeDto(
        BigDecimal width,
        BigDecimal height,
        BigDecimal depth
    ) {

    }

}