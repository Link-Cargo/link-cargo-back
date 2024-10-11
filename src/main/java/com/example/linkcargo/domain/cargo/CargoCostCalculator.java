package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import lombok.Getter;

public class CargoCostCalculator {

    private static final BigDecimal EXCHANGE_RATE = new BigDecimal("1320"); // 고정 환율 사용

    public static BigDecimal calculateTotalCost(List<CargoInfo> cargoInfos, String incotermsType, Integer freightCost) {
        BigDecimal totalCBM = calculateTotalCBM(cargoInfos);
        int totalExportQuantity = calculateTotalExportQuantity(cargoInfos);
        BigDecimal totalCargoValue = calculateTotalCargoValue(cargoInfos);

        QuotationDomesticExpense domesticExpense = calculateDomesticExpense(totalCBM);
        BigDecimal incotermsFOB = calculateIncotermsFOB(totalCargoValue, domesticExpense, totalExportQuantity);
        System.out.println(freightCost);

        QuotationOverseaExpense overseaExpense = calculateOverseaExpense(
            totalCBM, domesticExpense.getAMForAFS(), totalExportQuantity, incotermsFOB, incotermsType, freightCost);

        return domesticExpense.getTotalDomesticExpenses()
            .add(overseaExpense.getTotalOverseaExpenses())
            .multiply(EXCHANGE_RATE);
    }

    static CargoInfo convertToCargoInfo(CargoRequest cargoRequest) {
        return new CargoInfo(
            cargoRequest.getValue(),
            cargoRequest.getTotalQuantity(),
            cargoRequest.getQuantityPerBox(),
            cargoRequest.getBoxSize().getWidth(),
            cargoRequest.getBoxSize().getHeight(),
            cargoRequest.getBoxSize().getDepth()
        );
    }

    private static BigDecimal calculateTotalCBM(List<CargoInfo> cargoInfos) {
        return cargoInfos.stream()
            .map(cargoInfo -> {
                BigDecimal boxVolume = cargoInfo.boxWidth
                    .multiply(cargoInfo.boxHeight)
                    .multiply(cargoInfo.boxDepth);
                int totalBoxes = cargoInfo.totalQuantity / cargoInfo.quantityPerBox;
                return boxVolume.multiply(BigDecimal.valueOf(totalBoxes));
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static int calculateTotalExportQuantity(List<CargoInfo> cargoInfos) {
        return cargoInfos.stream()
            .mapToInt(cargoInfo -> cargoInfo.totalQuantity)
            .sum();
    }

    private static BigDecimal calculateTotalCargoValue(List<CargoInfo> cargoInfos) {
        return cargoInfos.stream()
            .map(cargoInfo -> cargoInfo.value)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private static QuotationDomesticExpense calculateDomesticExpense(BigDecimal totalCBM) {
        BigDecimal THC = totalCBM.multiply(BigDecimal.valueOf(6500))
            .divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
        BigDecimal CFS_CHARGE = totalCBM.multiply(BigDecimal.valueOf(6500))
            .divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
        BigDecimal WHARFAGE_FEE = totalCBM.multiply(BigDecimal.valueOf(210))
            .divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
        BigDecimal DOCUMENT_FEE = BigDecimal.valueOf(35000 * 1.1)
            .divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
        BigDecimal HANDLING_FEE = BigDecimal.valueOf(30000 * 1.1)
            .divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
        BigDecimal CUSTOMS_CLEARANCE_FEE = BigDecimal.valueOf(30000 * 1.1)
            .divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
        BigDecimal DOMESTIC_TRUCKING = BigDecimal.valueOf(150000 * 1.1)
            .divide(EXCHANGE_RATE, 2, RoundingMode.HALF_UP);
        BigDecimal AMForAFS = BigDecimal.valueOf(30);

        BigDecimal totalDomesticExpenses = THC.add(CFS_CHARGE)
            .add(WHARFAGE_FEE)
            .add(DOCUMENT_FEE)
            .add(HANDLING_FEE)
            .add(CUSTOMS_CLEARANCE_FEE)
            .add(DOMESTIC_TRUCKING)
            .add(AMForAFS);

        return new QuotationDomesticExpense(THC, CFS_CHARGE, WHARFAGE_FEE, DOCUMENT_FEE, HANDLING_FEE,
            CUSTOMS_CLEARANCE_FEE, DOMESTIC_TRUCKING, AMForAFS, totalDomesticExpenses);
    }

    private static BigDecimal calculateIncotermsFOB(BigDecimal totalCargoValue, QuotationDomesticExpense domesticExpense, int totalExportQuantity) {
        return totalCargoValue
            .add(domesticExpense.getTotalDomesticExpenses())
            .subtract(domesticExpense.getAMForAFS())
            .divide(BigDecimal.valueOf(totalExportQuantity), 2, RoundingMode.HALF_UP);
    }

    private static QuotationOverseaExpense calculateOverseaExpense(
        BigDecimal totalCBM,
        BigDecimal AMForAFS,
        Integer totalExportQuantity,
        BigDecimal incotermsFOB,
        String incotermsType,
        Integer freight
    ) {
        BigDecimal freightCost = totalCBM.multiply(BigDecimal.valueOf(freight));

        BigDecimal incotermsCFR = freightCost
            .add(AMForAFS)
            .divide(BigDecimal.valueOf(totalExportQuantity), 2, RoundingMode.HALF_UP)
            .add(incotermsFOB);

        BigDecimal cargoInsurance = incotermsCFR
            .multiply(BigDecimal.valueOf(1.1))
            .multiply(BigDecimal.valueOf(0.0004))
            .multiply(BigDecimal.valueOf(totalExportQuantity));

        BigDecimal inspectionFee = new BigDecimal("250.00");
        BigDecimal overseaTrucking = new BigDecimal("250.00");

        BigDecimal incotermsCIF = cargoInsurance
            .divide(BigDecimal.valueOf(totalExportQuantity), 2, RoundingMode.HALF_UP)
            .add(incotermsCFR);

        BigDecimal incotermsDAP = overseaTrucking
            .divide(BigDecimal.valueOf(totalExportQuantity), 2, RoundingMode.HALF_UP)
            .add(incotermsCIF);

        BigDecimal incotermsDDP = inspectionFee
            .divide(BigDecimal.valueOf(totalExportQuantity), 2, RoundingMode.HALF_UP)
            .add(incotermsDAP);

        if ("CFR".equals(incotermsType)) {
            cargoInsurance = BigDecimal.ZERO;
        } else if ("CIF".equals(incotermsType)) {
            // Do nothing, cargoInsurance is already calculated
        } else if ("DAP".equals(incotermsType) || "DDP".equals(incotermsType)) {
            cargoInsurance = BigDecimal.ZERO;
        }

        BigDecimal totalOverseaExpenses = freightCost
            .add(cargoInsurance)
            .add(inspectionFee)
            .add(overseaTrucking);

        return new QuotationOverseaExpense(freightCost, cargoInsurance, inspectionFee, overseaTrucking, totalOverseaExpenses);
    }

    // CargoInfo 클래스 정의
    public static class CargoInfo {
        public BigDecimal value;
        public int totalQuantity;
        public int quantityPerBox;
        public BigDecimal boxWidth;
        public BigDecimal boxHeight;
        public BigDecimal boxDepth;

        public CargoInfo(BigDecimal value, int totalQuantity, int quantityPerBox,
            BigDecimal boxWidth, BigDecimal boxHeight, BigDecimal boxDepth) {
            this.value = value;
            this.totalQuantity = totalQuantity;
            this.quantityPerBox = quantityPerBox;
            this.boxWidth = boxWidth;
            this.boxHeight = boxHeight;
            this.boxDepth = boxDepth;
        }
    }

    private static class QuotationDomesticExpense {
        private BigDecimal THC;
        private BigDecimal CFS_CHARGE;
        private BigDecimal WHARFAGE_FEE;
        private BigDecimal DOCUMENT_FEE;
        private BigDecimal HANDLING_FEE;
        private BigDecimal CUSTOMS_CLEARANCE_FEE;
        private BigDecimal DOMESTIC_TRUCKING;
        @Getter
        private BigDecimal AMForAFS;
        @Getter
        private BigDecimal totalDomesticExpenses;

        public QuotationDomesticExpense(BigDecimal THC, BigDecimal CFS_CHARGE, BigDecimal WHARFAGE_FEE,
            BigDecimal DOCUMENT_FEE, BigDecimal HANDLING_FEE, BigDecimal CUSTOMS_CLEARANCE_FEE,
            BigDecimal DOMESTIC_TRUCKING, BigDecimal AMForAFS, BigDecimal totalDomesticExpenses) {
            this.THC = THC;
            this.CFS_CHARGE = CFS_CHARGE;
            this.WHARFAGE_FEE = WHARFAGE_FEE;
            this.DOCUMENT_FEE = DOCUMENT_FEE;
            this.HANDLING_FEE = HANDLING_FEE;
            this.CUSTOMS_CLEARANCE_FEE = CUSTOMS_CLEARANCE_FEE;
            this.DOMESTIC_TRUCKING = DOMESTIC_TRUCKING;
            this.AMForAFS = AMForAFS;
            this.totalDomesticExpenses = totalDomesticExpenses;
        }

    }

    private static class QuotationOverseaExpense {
        private BigDecimal freightCost;
        private BigDecimal cargoInsurance;
        private BigDecimal inspectionFee;
        private BigDecimal overseaTrucking;
        @Getter
        private BigDecimal totalOverseaExpenses;

        public QuotationOverseaExpense(BigDecimal freightCost, BigDecimal cargoInsurance,
            BigDecimal inspectionFee, BigDecimal overseaTrucking,
            BigDecimal totalOverseaExpenses) {
            this.freightCost = freightCost;
            this.cargoInsurance = cargoInsurance;
            this.inspectionFee = inspectionFee;
            this.overseaTrucking = overseaTrucking;
            this.totalOverseaExpenses = totalOverseaExpenses;
        }

    }
}