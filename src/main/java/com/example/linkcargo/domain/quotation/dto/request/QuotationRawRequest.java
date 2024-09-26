package com.example.linkcargo.domain.quotation.dto.request;

import com.example.linkcargo.domain.quotation.Quotation;
import com.example.linkcargo.domain.quotation.QuotationStatus;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record QuotationRawRequest(
    @NotNull(message = "Cargo ID is mandatory")
    List<String> cargoIds

) {
    public Quotation toEntity(String userId) {
        Quotation.Freight freight = Quotation.Freight.builder()
            .scheduleId(null)
            .remark(null)
            .build();

        Quotation.Cost cost = Quotation.Cost.builder()
            .cargoIds(this.cargoIds)
            .chargeExport(null)
            .freightCost(null)
            .totalCost(null)
            .build();

        return Quotation.builder()
            .consignorId(userId)
            .forwarderId(null)
            .particulars(null)
            .originalQuotationId(null)
            .quotationStatus(QuotationStatus.RAW_SHEET)
            .freight(freight)
            .cost(cost)
            .build();
    }
}
