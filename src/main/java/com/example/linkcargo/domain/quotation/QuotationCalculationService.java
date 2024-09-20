package com.example.linkcargo.domain.quotation;

import com.example.linkcargo.domain.cargo.Cargo;
import com.example.linkcargo.domain.cargo.CargoRepository;
import com.example.linkcargo.domain.schedule.Schedule;
import com.example.linkcargo.domain.schedule.ScheduleRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.CargoHandler;
import com.example.linkcargo.global.response.exception.handler.QuotationHandler;
import com.example.linkcargo.global.response.exception.handler.ScheduleHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuotationCalculationService {

    @Value("${api-key.export-import-bok.secretKey}")
    private String apiKey;

    private final QuotationRepository quotationRepository;
    private final CargoRepository cargoRepository;
    private final ScheduleRepository scheduleRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    static // 화물 정보
    public class CargoBaseInfo {
        private BigDecimal cargoValue; // 물품 가액
        private Integer totalExportQuantity; // 총 수출 물품 수량
        private Integer totalBoxQuantity; // 총 박스 수량
        private Integer quantityPerBox; // 박스 당 제품 수량
        private Integer totalAmountInKRW; // 원화 환산 총액
        private BigDecimal unitPriceInForeignCurrency; // 외화 환산 단가
        private BigDecimal totalAmountInForeignCurrency; // 외화 환산 총액
        private BigDecimal boxWidth; // 박스 가로 길이
        private BigDecimal boxHeight; // 박스 세로 길이
        private BigDecimal boxDepth; // 박스 높이 길이
        private BigDecimal CBMPerBox; // 박스 당 CBM
        private BigDecimal totalCBM; // 총 CBM

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    static // 국내 발생 경비
    public class QuotationDomesticExpense {
        private BigDecimal THC; // THC
        private BigDecimal CFS_CHARGE; // CFS
        private BigDecimal WHARFAGE_FEE; // WHARFAGE
        private BigDecimal DOCUMENT_FEE; // DOCUMENT FEE
        private BigDecimal HANDLING_FEE; // H/C
        private BigDecimal CUSTOMS_CLEARANCE_FEE; // 통관 수수료
        private BigDecimal DOMESTIC_TRUCKING; // 국내 운송료
        private BigDecimal AMForAFS; // Automated Manifest Service
        private BigDecimal totalDomesticExpenses; // 국내 발생 총 경비
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Builder
    static // 국외 발생 경비
    public class QuotationOverseaExpense {
        private BigDecimal freightCost; // O/Freight
        private BigDecimal cargoInsurance; // 적하 보험료
        private BigDecimal inspectionFee; // 수입 통관 재비용
        private BigDecimal overseaTrucking; // 내륙 운송료
        private BigDecimal totalOverseaExpenses; // 국외 발생 총 경비
    }


    public List<CargoBaseInfo> processCargos(List<Cargo> cargos, Integer appliedExchangeRate) {
        BigDecimal exchangeRate = new BigDecimal(appliedExchangeRate);

        return cargos.stream()
            .map(cargo -> {
                BigDecimal cargoValue = cargo.getCargoInfo().getValue();
                Integer cargoQuantity = cargo.getCargoInfo().getTotalQuantity();
                Integer quantityPerBox = cargo.getCargoInfo().getQuantityPerBox();
                int totalBoxQuantity = cargoQuantity / quantityPerBox;
                BigDecimal unitPriceInForeignCurrency = cargoValue.divide(exchangeRate, 2, RoundingMode.HALF_UP);
                BigDecimal boxWidth = cargo.getCargoInfo().getBoxSize().getWidth();
                BigDecimal boxHeight = cargo.getCargoInfo().getBoxSize().getHeight();
                BigDecimal boxDepth = cargo.getCargoInfo().getBoxSize().getDepth();
                BigDecimal CBMPerBox = boxWidth.multiply(boxHeight).multiply(boxDepth);
                BigDecimal totalCBM = CBMPerBox.multiply(BigDecimal.valueOf(totalBoxQuantity));

                return CargoBaseInfo.builder()
                    .cargoValue(cargoValue)
                    .totalExportQuantity(cargoQuantity)
                    .totalBoxQuantity(totalBoxQuantity)
                    .quantityPerBox(quantityPerBox)
                    .totalAmountInKRW(260000)
                    .unitPriceInForeignCurrency(unitPriceInForeignCurrency)
                    .totalAmountInForeignCurrency(unitPriceInForeignCurrency.multiply(new BigDecimal(cargoQuantity)))
                    .boxWidth(boxWidth)
                    .boxHeight(boxHeight)
                    .boxDepth(boxDepth)
                    .CBMPerBox(CBMPerBox)
                    .totalCBM(totalCBM)
                    .build();
            })

            .collect(Collectors.toList());
    }

    public QuotationDomesticExpense calculateDomesticExpense(BigDecimal totalCBM, int exchange) {
        BigDecimal exchangeRate = new BigDecimal(exchange);

        BigDecimal THC = totalCBM.multiply(BigDecimal.valueOf(6500))
            .divide(exchangeRate, 2, RoundingMode.HALF_UP);
        BigDecimal CFS_CHARGE = totalCBM.multiply(BigDecimal.valueOf(6500))
            .divide(exchangeRate, 2, RoundingMode.HALF_UP);
        BigDecimal WHARFAGE_FEE = totalCBM.multiply(BigDecimal.valueOf(210))
            .divide(exchangeRate, 2, RoundingMode.HALF_UP);
        BigDecimal DOCUMENT_FEE = BigDecimal.valueOf(35000 * 1.1)
            .divide(exchangeRate, 2, RoundingMode.HALF_UP);
        BigDecimal HANDLING_FEE = BigDecimal.valueOf(30000 * 1.1)
            .divide(exchangeRate, 2, RoundingMode.HALF_UP);
        BigDecimal CUSTOMS_CLEARANCE_FEE = BigDecimal.valueOf(30000 * 1.1)
            .divide(exchangeRate, 2, RoundingMode.HALF_UP);
        BigDecimal DOMESTIC_TRUCKING = BigDecimal.valueOf(150000 * 1.1)
            .divide(exchangeRate, 2, RoundingMode.HALF_UP);
        BigDecimal AMForAFS = BigDecimal.valueOf(30);

        return QuotationDomesticExpense.builder()
            .THC(THC)
            .CFS_CHARGE(CFS_CHARGE)
            .WHARFAGE_FEE(WHARFAGE_FEE)
            .DOCUMENT_FEE(DOCUMENT_FEE)
            .HANDLING_FEE(HANDLING_FEE)
            .CUSTOMS_CLEARANCE_FEE(CUSTOMS_CLEARANCE_FEE)
            .DOMESTIC_TRUCKING(DOMESTIC_TRUCKING)
            .AMForAFS(AMForAFS)
            .totalDomesticExpenses(THC.add(CFS_CHARGE)
                .add(WHARFAGE_FEE)
                .add(DOCUMENT_FEE)
                .add(HANDLING_FEE)
                .add(CUSTOMS_CLEARANCE_FEE)
                .add(DOMESTIC_TRUCKING)
                .add(AMForAFS))
            .build();
    }

    public QuotationOverseaExpense calculateOverseaExpense(
        BigDecimal totalCBM,
        BigDecimal AMForAFS,
        Integer totalExportQuantity,
        BigDecimal incotermsPOB,
        String incotermsType,
        Integer freight
    ) {
        BigDecimal freightCost = totalCBM.multiply(BigDecimal.valueOf(freight));

        BigDecimal incotermsCFR = freightCost
            .add(AMForAFS)
            .divide(BigDecimal.valueOf(totalExportQuantity), 2, RoundingMode.HALF_UP)
            .add(incotermsPOB);

        BigDecimal cargoInsurance = incotermsCFR
            .multiply(BigDecimal.valueOf(1.1))
            .multiply(BigDecimal.valueOf(0.0004))
            .multiply(BigDecimal.valueOf(totalExportQuantity));

        BigDecimal inspectionFee = new BigDecimal("250.00");
        BigDecimal overseaTrucking =  new BigDecimal("250.00");


        BigDecimal incoterms = null;


        BigDecimal incotermsCIF = cargoInsurance
            .divide(BigDecimal.valueOf(totalExportQuantity), 2, RoundingMode.HALF_UP)
            .add(incotermsCFR);

        BigDecimal incotermsDAP = overseaTrucking
            .divide(BigDecimal.valueOf(totalExportQuantity), 2, RoundingMode.HALF_UP)
            .add(incotermsCIF);

        BigDecimal incotermsDDP = inspectionFee
            .divide(BigDecimal.valueOf(totalExportQuantity), 2, RoundingMode.HALF_UP)
            .add(incotermsDAP);

        // 인코텀즈 비용에 따른 국외 발생 경비의 차이
        // CIF 선택 했을 시에만 적하 보험료 발생, 그 외에는 적하 보험료 0원 처리
        if (Objects.equals(incotermsType, "CFR")) {
            incoterms = incotermsCFR;
            cargoInsurance = BigDecimal.valueOf(0);

        } else if (Objects.equals(incotermsType, "CIF")) {
            incoterms = incotermsCIF;

        } else if (Objects.equals(incotermsType, "DAP")) {
            incoterms = incotermsDAP;
            cargoInsurance = BigDecimal.valueOf(0);

        } else {
            incoterms = incotermsDDP;
            cargoInsurance = BigDecimal.valueOf(0);
        }

        BigDecimal totalOverseaExpenses = freightCost
            .add(cargoInsurance)
            .add(inspectionFee)
            .add(overseaTrucking);

        return QuotationOverseaExpense.builder()
            .freightCost(freightCost)
            .cargoInsurance(cargoInsurance)
            .inspectionFee(inspectionFee)
            .overseaTrucking(overseaTrucking)
            .totalOverseaExpenses(totalOverseaExpenses)
            .build();
    }

    // 실시간 환율 라이브러리
    public String getExchange() {
        String url = "https://www.koreaexim.go.kr/site/program/financial/exchangeJSON" +
            "?authkey=" + apiKey +
            "&data=AP01";

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            for (JsonNode node : rootNode) {
                if ("미국 달러".equals(node.get("cur_nm").asText())) {
                    return node.get("kftc_bkpr").asText();
                }
            }
        } catch (Exception ignored) {
        }
        return "1320";
    }

    public BigDecimal calculateTotalCost(Quotation inputQuotation, Integer freightCost) {
        Quotation quotation = quotationRepository.findById(inputQuotation.getId())
            .orElseThrow(() -> new QuotationHandler(ErrorStatus.QUOTATION_NOT_FOUND));

        List<String> cargoIds = quotation.getCost().getCargoIds();
        List<Cargo> cargos = cargoIds.stream()
            .map(cargoId -> cargoRepository.findById(cargoId)
                .orElseThrow(() -> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND)))
            .toList();

        Cargo firstCargo = cargos.get(0);

        // 화물정보
        BigDecimal totalWeight = cargos.stream()
            .map(cargo -> cargo.getCargoInfo().getWeight())
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 환율
        int applied_exchange_rate = Integer.parseInt(getExchange());

        // 자세한 화물 정보
        List<CargoBaseInfo> cargoBaseInfos = processCargos(cargos, applied_exchange_rate);

        // 모든 화물의 CBM
        BigDecimal cargosTotalCBM = cargoBaseInfos.stream()
            .map(CargoBaseInfo::getTotalCBM)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 국내 발생 경비
        QuotationDomesticExpense domesticExpense = calculateDomesticExpense(cargosTotalCBM, applied_exchange_rate);


        // 모든 화물의 외화 환산 총액
        BigDecimal cargosTotalAmountInForeignCurrency = cargoBaseInfos.stream()
            .map(CargoBaseInfo::getTotalAmountInForeignCurrency)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 모든 화물의 총 수출 물품 수량의 합
        Integer cargosTotalExportQuantity = cargoBaseInfos.stream()
            .map(CargoBaseInfo::getTotalExportQuantity)
            .reduce(0, Integer::sum);

        // Incoterms 개당 원가
        BigDecimal incotermsFOB = (domesticExpense.getTotalDomesticExpenses())
            .subtract(domesticExpense.getAMForAFS())
            .add(cargosTotalAmountInForeignCurrency)
            .divide(BigDecimal.valueOf(cargosTotalExportQuantity), 2, RoundingMode.HALF_UP);


        // 국외 발생 경비
        QuotationOverseaExpense overseaExpense
            = calculateOverseaExpense(cargosTotalCBM, domesticExpense.getAMForAFS(), cargosTotalExportQuantity, incotermsFOB, firstCargo.getIncoterms(),
            freightCost);

        BigDecimal domesticExpenseTotalCost = domesticExpense.getTotalDomesticExpenses();
        BigDecimal overseaExpenseTotalCost = overseaExpense.getTotalOverseaExpenses();

        return domesticExpenseTotalCost.add(overseaExpenseTotalCost).multiply(
            BigDecimal.valueOf(applied_exchange_rate));
    }
    @Transactional
    public void updateQuotationByAlgorithm(Quotation quotation) {
        List<String> cargoIds = quotation.getCost().getCargoIds();

        Schedule schedule = scheduleRepository.findById(Long.valueOf(quotation.getFreight().getScheduleId()))
            .orElseThrow(() -> new ScheduleHandler(ErrorStatus.SCHEDULE_NOT_FOUND));
        // todo
        // 임시 운임 비용 -> 운임 비용 예측 AI API를 사용해 반환 예정
        Integer freightCost = 10;

        BigDecimal totalCost = calculateTotalCost(quotation, freightCost);

        int applied_exchange_rate = Integer.parseInt(getExchange());

        Quotation createdQuotation = Quotation.builder()
            .quotationStatus(QuotationStatus.PREDICTION_SHEET)
            .consignorId(quotation.getConsignorId())
            .originalQuotationId(quotation.getId())
            .rawQuotationId(quotation.getRawQuotationId())
            .freight(Quotation.Freight.builder()
                .scheduleId(String.valueOf(schedule.getId()))
                .build())
            .cost(Quotation.Cost.builder()
                .cargoIds(cargoIds)
                .totalCost(totalCost.multiply(BigDecimal.valueOf(applied_exchange_rate)))
                .build())
            .build();

        quotationRepository.save(createdQuotation);

    }
}
