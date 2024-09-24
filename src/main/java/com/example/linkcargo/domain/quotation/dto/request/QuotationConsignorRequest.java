package com.example.linkcargo.domain.quotation.dto.request;

import com.example.linkcargo.domain.quotation.Quotation;
import com.example.linkcargo.domain.quotation.QuotationStatus;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record QuotationConsignorRequest(
        @NotNull(message = "Schedule ID is mandatory")
        Long scheduleId,

        @NotNull(message = "RawQuotation ID is mandatory")
        String rawQuotationId

) {

    public Quotation toEntity(String userId, List<String> cargoIds) {
        Quotation.Freight freight = Quotation.Freight.builder()
                .scheduleId(String.valueOf(this.scheduleId))
                .remark(null)
                .build();

        Quotation.Cost cost = Quotation.Cost.builder()
                .cargoIds(cargoIds)
                .chargeExport(null)
                .freightCost(null)
                .totalCost(null)
                .build();

        return Quotation.builder()
                .consignorId(userId)
                .forwarderId(null)
                .particulars(null)
                .originalQuotationId(null)
                .rawQuotationId(this.rawQuotationId)
                .quotationStatus(QuotationStatus.BASIC_INFO)
                .freight(freight)
                .cost(cost)
                .build();
    }
}