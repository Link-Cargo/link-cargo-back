package com.example.linkcargo.domain.quotation.dto.response;


import com.example.linkcargo.domain.cargo.Cargo;
import com.example.linkcargo.domain.quotation.Quotation;
import com.example.linkcargo.domain.schedule.Schedule;
import java.math.BigDecimal;
import lombok.Builder;

@Builder
public record QuotationInfoResponse(
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
            .carrier(schedule.getCarrier())
            .exportPort(schedule.getExportPort().getName())
            .importPort(schedule.getImportPort().getName())
            .freightLCL(quotation.getCost().getFreightCost().getLCL())
            .freightCBM(quotation.getCost().getFreightCost().getCBM())
            .freightCost(quotation.getCost().getFreightCost().getSUM())
            .transitTime(schedule.getTransitTime())
            .scheduleRemark(quotation.getFreight().getRemark())
            .THC(quotation.getCost().getChargeExport().getTHC())
            .CIC(quotation.getCost().getChargeExport().getCIC())
            .DO_FEE(quotation.getCost().getChargeExport().getDO_FEE())
            .HANDLING_FEE(quotation.getCost().getChargeExport().getHANDLING_FEE())
            .CFS_CHARGE(quotation.getCost().getChargeExport().getCFS_CHARGE())
            .LIFT_STATUS(quotation.getCost().getChargeExport().getLIFT_STATUS())
            .CUSTOMS_CLEARANCE_FEE(quotation.getCost().getChargeExport().getCUSTOMS_CLEARANCE_FEE())
            .WARFAGE_FEE(quotation.getCost().getChargeExport().getWARFAGE_FEE())
            .TRUCKING(quotation.getCost().getChargeExport().getTRUCKING())
            .chargeExportCost(quotation.getCost().getChargeExport().getSUM())
            .totalCost(quotation.getCost().getTotalCost())
            .build();
    }
}
