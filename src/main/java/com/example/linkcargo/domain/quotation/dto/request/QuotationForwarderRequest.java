package com.example.linkcargo.domain.quotation.dto.request;

import com.example.linkcargo.domain.quotation.Quotation;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record QuotationForwarderRequest(

    @NotNull(message = "QuotationId is mandatory")
    String quotationId,

    @NotNull(message = "Freight LCL is mandatory")
    BigDecimal freightLCL,

    @NotNull(message = "Freight CBM is mandatory")
    BigDecimal freightCBM,

    String freightRemark,

    @NotNull(message = "THC LCL is mandatory")
    BigDecimal thcLCL,
    String thcRemark,

    @NotNull(message = "CIC LCL is mandatory")
    BigDecimal cicLCL,
    String cicRemark,

    @NotNull(message = "DO FEE LCL is mandatory")
    BigDecimal doFeeLCL,
    String doFeeRemark,

    @NotNull(message = "HANDLING FEE LCL is mandatory")
    BigDecimal handlingFeeLCL,
    String handlingFeeRemark,

    @NotNull(message = "CFS CHARGE LCL is mandatory")
    BigDecimal cfsChargeLCL,
    String cfsChargeRemark,

    @NotNull(message = "LIFT ON/OFF LCL is mandatory")
    BigDecimal liftOnOffLCL,
    String liftOnOffRemark,

    @NotNull(message = "CUSTOMS CLEARANCE FEE LCL is mandatory")
    BigDecimal customsClearanceFeeLCL,
    String customsClearanceFeeRemark,

    @NotNull(message = "WARFAGE FEE LCL is mandatory")
    BigDecimal warfageFeeLCL,
    String warfageFeeRemark,

    @NotNull(message = "TRUCKING LCL is mandatory")
    BigDecimal truckingLCL,
    String truckingRemark


) {
    public Quotation updateQuotation(Quotation quotation, String userId) {

        // Freight 업데이트
        Quotation.Freight freight = quotation.getFreight();
        if (freight != null) {
            quotation.setFreight(
                Quotation.Freight.builder()
                    .scheduleId(quotation.getFreight().getScheduleId())
                    .remark(this.freightRemark)
                    .build()
            );
        }

        // Cost 업데이트
        Quotation.Cost exsistingCost = quotation.getCost();
        if (exsistingCost != null) {
            Quotation.ChargeExport chargeExport = exsistingCost.getChargeExport();
            if (chargeExport == null) {
                chargeExport = Quotation.ChargeExport.builder().build();
            }

            BigDecimal costSum = BigDecimal.ZERO;
            costSum = costSum.add(this.freightLCL != null ? this.freightLCL : BigDecimal.ZERO);
            costSum = costSum.add(this.thcLCL != null ? this.thcLCL : BigDecimal.ZERO);
            costSum = costSum.add(this.cicLCL != null ? this.cicLCL : BigDecimal.ZERO);
            costSum = costSum.add(this.doFeeLCL != null ? this.doFeeLCL : BigDecimal.ZERO);
            costSum = costSum.add(this.handlingFeeLCL != null ? this.handlingFeeLCL : BigDecimal.ZERO);
            costSum = costSum.add(this.cfsChargeLCL != null ? this.cfsChargeLCL : BigDecimal.ZERO);
            costSum = costSum.add(this.liftOnOffLCL != null ? this.liftOnOffLCL : BigDecimal.ZERO);
            costSum = costSum.add(this.customsClearanceFeeLCL != null ? this.customsClearanceFeeLCL : BigDecimal.ZERO);
            costSum = costSum.add(this.warfageFeeLCL != null ? this.warfageFeeLCL : BigDecimal.ZERO);
            costSum = costSum.add(this.truckingLCL != null ? this.truckingLCL : BigDecimal.ZERO);

            chargeExport = Quotation.ChargeExport.builder()
                .THC(Quotation.TEU.builder()
                    .unit("CMB/CNTR")
                    .LCL(this.thcLCL)
                    .remark(this.thcRemark)
                    .build())
                .CIC(Quotation.TEU.builder()
                    .unit("CMB/CNTR")
                    .LCL(this.cicLCL)
                    .remark(this.cicRemark)
                    .build())
                .DO_FEE(Quotation.TEU.builder()
                    .unit("BL")
                    .LCL(this.doFeeLCL)
                    .remark(this.doFeeRemark)
                    .build())
                .HANDLING_FEE(Quotation.TEU.builder()
                    .unit("BL")
                    .LCL(this.handlingFeeLCL)
                    .remark(this.handlingFeeRemark)
                    .build())
                .CFS_CHARGE(Quotation.TEU.builder()
                    .unit("CBM")
                    .LCL(this.cfsChargeLCL)
                    .remark(this.cfsChargeRemark)
                    .build())
                .LIFT_STATUS(Quotation.TEU.builder()
                    .unit("CMB/CNTR")
                    .LCL(this.liftOnOffLCL)
                    .remark(this.liftOnOffRemark)
                    .build())
                .CUSTOMS_CLEARANCE_FEE(Quotation.TEU.builder()
                    .unit("SHEET/CNTR")
                    .LCL(this.customsClearanceFeeLCL)
                    .remark(this.customsClearanceFeeRemark)
                    .build())
                .WARFAGE_FEE(Quotation.TEU.builder()
                    .unit("CNTR")
                    .LCL(this.warfageFeeLCL)
                    .remark(this.warfageFeeRemark)
                    .build())
                .TRUCKING(Quotation.TEU.builder()
                    .unit("TRUK")
                    .LCL(this.truckingLCL)
                    .remark(this.truckingRemark)
                    .build())
                .SUM(costSum)
                .build();

            BigDecimal freightSum =
                this.freightLCL != null ? this.freightLCL.multiply(this.freightCBM) : null;

            Quotation.FreightCost freightCost = Quotation.FreightCost.builder()
                .LCL(this.freightLCL)
                .CBM(this.freightCBM)
                .SUM(freightSum)
                .build();

            BigDecimal totalCost = BigDecimal.ZERO;
            totalCost = totalCost.add(costSum);
            totalCost = totalCost.add(freightSum);
            

            Quotation.Cost cost = Quotation.Cost.builder()
                .cargoIds(quotation.getCost().getCargoIds())
                .chargeExport(chargeExport)
                .freightCost(freightCost)
                .totalCost(totalCost)
                .build();

            quotation.setCost(cost);
            quotation.setForwarderId(userId);


        }
        return quotation;
    }

}
