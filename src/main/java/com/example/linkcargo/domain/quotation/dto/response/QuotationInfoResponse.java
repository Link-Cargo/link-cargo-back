package com.example.linkcargo.domain.quotation.dto.response;


import com.example.linkcargo.domain.quotation.Quotation;
import com.example.linkcargo.domain.quotation.QuotationStatus;
import com.example.linkcargo.domain.schedule.Schedule;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record QuotationInfoResponse(
    String quotationId,
    String originalQuotationId,
    QuotationStatus quotationStatus,
    String carrier,
    String exportPort,
    String importPort,
    BigDecimal freightLCL,
    BigDecimal freightCBM,
    BigDecimal freightCost,
    Integer transitTime,
    String scheduleRemark,
    Quotation.TEU THC,
    Quotation.TEU CIC,

    Quotation.TEU DO_FEE,

    Quotation.TEU HANDLING_FEE,

    Quotation.TEU CFS_CHARGE,

    Quotation.TEU LIFT_STATUS,

    Quotation.TEU CUSTOMS_CLEARANCE_FEE,

    Quotation.TEU WARFAGE_FEE,

    Quotation.TEU TRUCKING,

    BigDecimal chargeExportCost,
    BigDecimal totalCost

) {

    public static QuotationInfoResponse fromEntity(Quotation quotation, Schedule schedule) {
        return QuotationInfoResponse.builder()
            .quotationId(quotation.getId())
            .originalQuotationId(quotation.getOriginalQuotationId())
            .quotationStatus(quotation.getQuotationStatus())
            .carrier(schedule != null ? schedule.getCarrier() : null)
            .exportPort(schedule != null && schedule.getExportPort() != null ? schedule.getExportPort().getName() : null)
            .importPort(schedule != null && schedule.getImportPort() != null ? schedule.getImportPort().getName() : null)
            .freightLCL(quotation.getCost() != null && quotation.getCost().getFreightCost() != null ? quotation.getCost().getFreightCost().getLCL() : null)
            .freightCBM(quotation.getCost() != null && quotation.getCost().getFreightCost() != null ? quotation.getCost().getFreightCost().getCBM() : null)
            .freightCost(quotation.getCost() != null && quotation.getCost().getFreightCost() != null ? quotation.getCost().getFreightCost().getSUM() : null)
            .transitTime(schedule != null ? schedule.getTransitTime() : null)
            .scheduleRemark(quotation.getFreight() != null ? quotation.getFreight().getRemark() : null)
            .THC(quotation.getCost() != null && quotation.getCost().getChargeExport() != null ? quotation.getCost().getChargeExport().getTHC() : null)
            .CIC(quotation.getCost() != null && quotation.getCost().getChargeExport() != null ? quotation.getCost().getChargeExport().getCIC() : null)
            .DO_FEE(quotation.getCost() != null && quotation.getCost().getChargeExport() != null ? quotation.getCost().getChargeExport().getDO_FEE() : null)
            .HANDLING_FEE(quotation.getCost() != null && quotation.getCost().getChargeExport() != null ? quotation.getCost().getChargeExport().getHANDLING_FEE() : null)
            .CFS_CHARGE(quotation.getCost() != null && quotation.getCost().getChargeExport() != null ? quotation.getCost().getChargeExport().getCFS_CHARGE() : null)
            .LIFT_STATUS(quotation.getCost() != null && quotation.getCost().getChargeExport() != null ? quotation.getCost().getChargeExport().getLIFT_STATUS() : null)
            .CUSTOMS_CLEARANCE_FEE(quotation.getCost() != null && quotation.getCost().getChargeExport() != null ? quotation.getCost().getChargeExport().getCUSTOMS_CLEARANCE_FEE() : null)
            .WARFAGE_FEE(quotation.getCost() != null && quotation.getCost().getChargeExport() != null ? quotation.getCost().getChargeExport().getWARFAGE_FEE() : null)
            .TRUCKING(quotation.getCost() != null && quotation.getCost().getChargeExport() != null ? quotation.getCost().getChargeExport().getTRUCKING() : null)
            .chargeExportCost(quotation.getCost() != null && quotation.getCost().getChargeExport() != null ? quotation.getCost().getChargeExport().getSUM() : null)
            .totalCost(quotation.getCost() != null ? quotation.getCost().getTotalCost() : null)
            .build();
    }
}
