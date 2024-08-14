package com.example.linkcargo.domain.dashboard;

import com.example.linkcargo.domain.cargo.CargoRepository;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardPredictionResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardQuotationCompareResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardQuotationResponse;
import com.example.linkcargo.domain.forwarding.Forwarding;
import com.example.linkcargo.domain.forwarding.ForwardingRepository;
import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.domain.port.PortRepository;
import com.example.linkcargo.domain.prediction.Prediction;
import com.example.linkcargo.domain.prediction.PredictionRepository;
import com.example.linkcargo.domain.quotation.Quotation;
import com.example.linkcargo.domain.quotation.QuotationRepository;
import com.example.linkcargo.domain.quotation.QuotationStatus;
import com.example.linkcargo.domain.quotation.dto.response.QuotationInfoResponse;
import com.example.linkcargo.domain.schedule.Schedule;
import com.example.linkcargo.domain.schedule.ScheduleRepository;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.PortHandler;
import com.example.linkcargo.global.response.exception.handler.QuotationHandler;
import com.example.linkcargo.global.response.exception.handler.ScheduleHandler;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final QuotationRepository quotationRepository;
    private final CargoRepository cargoRepository;
    private final ScheduleRepository scheduleRepository;
    private final ForwardingRepository forwardingRepository;
    private final UserRepository userRepository;
    private final PredictionRepository predictionRepository;
    private final PortRepository portRepository;

    public Integer convertToInteger(BigDecimal value) {
        return value.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    // todo
    // 견적서 조회 시 여러 개의 scheduleId가 있는데 어떤 기준으로 판별할 것 인지
    public DashboardQuotationResponse getTheCheapestQuotation(Long consignorId) {
        List<Quotation> quotations = quotationRepository.findQuotationsByConsignorId(String.valueOf(consignorId));

        Quotation lowestCostQuotation = quotations.stream()
            .min(Comparator.comparing(quotation -> quotation.getCost().getTotalCost()))
            .orElseThrow(() -> new QuotationHandler(ErrorStatus.QUOTATION_NOT_FOUND));


        Schedule schedule = scheduleRepository.findById(
                Long.valueOf(lowestCostQuotation.getFreight().getScheduleId()))
            .orElseThrow(()-> new ScheduleHandler(ErrorStatus.SCHEDULE_NOT_FOUND));

        QuotationInfoResponse quotationInfoResponse = QuotationInfoResponse.fromEntity(lowestCostQuotation, schedule);

        User user = userRepository.findById(Long.valueOf(lowestCostQuotation.getForwarderId()))
            .orElseThrow(()-> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        BigDecimal totalCost = (lowestCostQuotation.getCost().getTotalCost()
            .setScale(1, RoundingMode.HALF_UP));

        return DashboardQuotationResponse.fromEntity(user, quotationInfoResponse, totalCost);
    }

    public DashboardQuotationCompareResponse getQuotationsForComparing(Long consignorId, String scheduleId) {
        List<Quotation> quotations
            = quotationRepository.findQuotationsByConsignorIdAndFreight_ScheduleIdAndQuotationStatus(
                String.valueOf(consignorId), scheduleId,QuotationStatus.DETAIL_INFO);

        List<DashboardQuotationResponse> dashboardQuotationResponses = quotations.stream()
            .map(quotation -> {

                Schedule schedule = scheduleRepository.findById(
                        Long.valueOf(quotation.getFreight().getScheduleId()))
                    .orElseThrow(() -> new ScheduleHandler(ErrorStatus.SCHEDULE_NOT_FOUND));

                QuotationInfoResponse quotationInfoResponse = QuotationInfoResponse.fromEntity(
                    quotation, schedule);

                User user = userRepository.findById(Long.valueOf(quotation.getForwarderId()))
                    .orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

                BigDecimal totalCost = quotation.getCost().getTotalCost()
                    .setScale(1, RoundingMode.HALF_UP);

                return DashboardQuotationResponse.fromEntity(user, quotationInfoResponse,
                    totalCost);

            })
            .toList();

        List<Map<String, Integer>> thcCostList = new ArrayList<>();
        List<Map<String, Integer>> handlingCostList = new ArrayList<>();
        List<Map<String, Integer>> cfsCostList = new ArrayList<>();
        List<Map<String, Integer>> liftStatusCostList = new ArrayList<>();
        List<Map<String, Integer>> customsClearanceCostList = new ArrayList<>();
        List<Map<String, Integer>> truckingCostList = new ArrayList<>();

        for (Quotation quotation : quotations) {
            Quotation.ChargeExport chargeExport = quotation.getCost().getChargeExport();
            String forwarderId = quotation.getForwarderId();
            User user = userRepository.findById(Long.valueOf(forwarderId))
                .orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

            Forwarding forwarding = user.getForwarding();
            String forwardingFirmName = forwarding.getFirmName();

            thcCostList.add(
                Map.of(forwardingFirmName, convertToInteger(chargeExport.getTHC().getLCL())));
            handlingCostList.add(Map.of(forwardingFirmName,
                convertToInteger(chargeExport.getHANDLING_FEE().getLCL())));
            cfsCostList.add(Map.of(forwardingFirmName,
                convertToInteger(chargeExport.getCFS_CHARGE().getLCL())));
            liftStatusCostList.add(Map.of(forwardingFirmName,
                convertToInteger(chargeExport.getLIFT_STATUS().getLCL())));
            customsClearanceCostList.add(Map.of(forwardingFirmName,
                convertToInteger(chargeExport.getCUSTOMS_CLEARANCE_FEE().getLCL())));
            truckingCostList.add(
                Map.of(forwardingFirmName, convertToInteger(chargeExport.getTRUCKING().getLCL())));

        }

        Map<String, List<Map<String, Integer>>> compareCostMap = Map.of(
            "thcCost", thcCostList,
            "handlingCost", handlingCostList,
            "cfsCost", cfsCostList,
            "liftStatusCost", liftStatusCostList,
            "customsClearanceCost", customsClearanceCostList,
            "truckingCost", truckingCostList
        );

        return DashboardQuotationCompareResponse.fromEntity(dashboardQuotationResponses, compareCostMap);

    }

    public DashboardPredictionResponse getPredictionInfo(Long exportPortId, Long importPortId) {
        LocalDate today = LocalDate.now();

        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        LocalDate sixMonthsLater = today.plusMonths(6);
        int endYear = sixMonthsLater.getYear();
        int endMonth = sixMonthsLater.getMonthValue();

        List<Prediction> predictions = predictionRepository.findPredictionsWithinPeriod(
            currentYear, currentMonth, endYear, endMonth);

        Map<Pair<String,String>,Integer> predictionList = predictions.stream()
            .collect(Collectors.toMap(
                prediction -> Pair.of(
                    String.valueOf(prediction.getYear()),
                    String.valueOf(prediction.getMonth())
                ),
                prediction -> Integer.parseInt(prediction.getFreightCostIndex()),
                (v1, v2) -> v1));

        Port exportPort = portRepository.findById(exportPortId)
            .orElseThrow(() -> new PortHandler(ErrorStatus.EXPORT_PORT_NOT_FOUND));
        Port importPort = portRepository.findById(importPortId)
            .orElseThrow(() -> new PortHandler(ErrorStatus.IMPORT_PORT_NOT_FOUND));

        return DashboardPredictionResponse.fromEntity(exportPort.getName(), importPort.getName(), predictionList);
    }
}
