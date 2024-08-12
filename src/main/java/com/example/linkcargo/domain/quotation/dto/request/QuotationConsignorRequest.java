package com.example.linkcargo.domain.quotation.dto.request;

import com.example.linkcargo.domain.quotation.Quotation;
import com.example.linkcargo.domain.quotation.QuotationStatus;
import jakarta.validation.constraints.NotNull;

public record QuotationConsignorRequest(
        @NotNull(message = "Schedule ID is mandatory")
        Long scheduleId,

        @NotNull(message = "Cargo ID is mandatory")
        String cargoId,

        String particulars
) {

    public Quotation toEntity(String userId) {
        Quotation.Freight freight = Quotation.Freight.builder()
                .scheduleId(String.valueOf(this.scheduleId))
                .remark(null)
                .build();

        Quotation.Cost cost = Quotation.Cost.builder()
                .cargoId(this.cargoId)
                .chargeExport(null)
                .freightCost(null)
                .totalCost(null)
                .build();

        return Quotation.builder()
                .consignorId(userId)
                .forwarderId(null)
                .quotationStatus(QuotationStatus.BASIC_INFO)
                .freight(freight)
                .cost(cost)
                .particulars(this.particulars)
                .build();
    }
}