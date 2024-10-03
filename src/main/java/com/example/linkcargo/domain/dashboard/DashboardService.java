package com.example.linkcargo.domain.dashboard;

import com.example.linkcargo.domain.cargo.Cargo;
import com.example.linkcargo.domain.cargo.CargoRepository;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardNewsResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardPortCongestionResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardPredictionReasonResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardPredictionReasonResponse.PredictionReason;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardPredictionResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardQuotationCompareResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardQuotationResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardRawQuotationResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardRecommendationResponse;
import com.example.linkcargo.domain.forwarding.Forwarding;
import com.example.linkcargo.domain.forwarding.ForwardingRepository;
import com.example.linkcargo.domain.news.News;
import com.example.linkcargo.domain.news.NewsRepository;
import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.domain.port.PortRepository;
import com.example.linkcargo.domain.prediction.Prediction;
import com.example.linkcargo.domain.prediction.PredictionRepository;
import com.example.linkcargo.domain.quotation.Quotation;
import com.example.linkcargo.domain.quotation.QuotationCalculationService;
import com.example.linkcargo.domain.quotation.QuotationCalculationService.CargoBaseInfo;
import com.example.linkcargo.domain.quotation.QuotationRepository;
import com.example.linkcargo.domain.quotation.QuotationStatus;
import com.example.linkcargo.domain.quotation.dto.response.QuotationInfoResponse;
import com.example.linkcargo.domain.schedule.Schedule;
import com.example.linkcargo.domain.schedule.ScheduleRepository;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.CargoHandler;
import com.example.linkcargo.global.response.exception.handler.PortHandler;
import com.example.linkcargo.global.response.exception.handler.QuotationHandler;
import com.example.linkcargo.global.response.exception.handler.ScheduleHandler;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Value;
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
    private final UserRepository userRepository;
    private final PredictionRepository predictionRepository;
    private final PortRepository portRepository;
    private final NewsRepository newsRepository;
    private final QuotationCalculationService quotationCalculationService;
    private final OpenAiService openAiService;



    public Integer convertToInteger(BigDecimal value) {
        return value.setScale(0, RoundingMode.HALF_UP).intValue();
    }

    private String convertListToString(List<Map<String, Integer>> costList) {
        return costList.stream()
            .map(map -> map.entrySet().stream()
                .map(entry -> entry.getKey() + ": " + entry.getValue())
                .collect(Collectors.joining(", ")))
            .collect(Collectors.joining(" | "));
    }

    public DashboardRawQuotationResponse getRawQuotations(Long userId) {
        List<Quotation> quotations = quotationRepository.findByConsignorIdAndQuotationStatus(userId.toString(), QuotationStatus.RAW_SHEET);

        LocalDate currentDate = LocalDate.now();

        List<DashboardRawQuotationResponse.RawQuotationInfo> rawQuotationInfoList = quotations.stream()
            .filter(quotation -> !quotation.getCost().getCargoIds().isEmpty())
            .map(quotation -> {
                String firstCargoId = quotation.getCost().getCargoIds().get(0);
                Cargo cargo = cargoRepository.findById(firstCargoId).orElseThrow(() ->
                    new CargoHandler(ErrorStatus.CARGO_NOT_FOUND));
                Port exportPort = portRepository.findById(cargo.getExportPortId())
                    .orElseThrow(() -> new PortHandler(ErrorStatus.EXPORT_PORT_NOT_FOUND));
                Port importPort = portRepository.findById(cargo.getImportPortId())
                    .orElseThrow(() -> new PortHandler(ErrorStatus.IMPORT_PORT_NOT_FOUND));
                return DashboardRawQuotationResponse.RawQuotationInfo.fromEntity(quotation, cargo, exportPort, importPort);
            })
            .filter(info -> info.ETD().isAfter(currentDate))
            .collect(Collectors.toList());

        return DashboardRawQuotationResponse.fromEntity(rawQuotationInfoList);
    }

    public DashboardQuotationResponse getTheCheapestQuotation(String rawQuotationId) {
        List<Quotation> quotations
            = quotationRepository.findQuotationsByRawQuotationIdAndQuotationStatus(rawQuotationId, QuotationStatus.DETAIL_INFO);

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
            .setScale(1, RoundingMode.HALF_UP)).multiply(BigDecimal.valueOf(1320));

        String particulars = lowestCostQuotation.getParticulars();
        return DashboardQuotationResponse.fromEntity(user, quotationInfoResponse, totalCost, particulars);
    }

    public DashboardQuotationCompareResponse getQuotationsForComparing(String rawQuotationId) {
        List<Quotation> quotations
            = quotationRepository.findQuotationsByRawQuotationIdAndQuotationStatus(rawQuotationId, QuotationStatus.DETAIL_INFO);

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
                    .setScale(1, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(1320));

                String particulars = quotation.getParticulars();

                return DashboardQuotationResponse.fromEntity(user, quotationInfoResponse,
                    totalCost, particulars);

            })
            .toList();

        List<Map<String, Integer>> thcCostList = new ArrayList<>();
        List<Map<String, Integer>> handlingCostList = new ArrayList<>();
        List<Map<String, Integer>> cfsCostList = new ArrayList<>();
        List<Map<String, Integer>> liftStatusCostList = new ArrayList<>();
        List<Map<String, Integer>> customsClearanceCostList = new ArrayList<>();
        List<Map<String, Integer>> truckingCostList = new ArrayList<>();
        List<Map<String, Integer>> cicCostList = new ArrayList<>();
        List<Map<String, Integer>> dofeeCostList = new ArrayList<>();
        List<Map<String, Integer>> warfageCostList = new ArrayList<>();

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
            cicCostList.add(
                Map.of(forwardingFirmName, convertToInteger(chargeExport.getCIC().getLCL())));
            dofeeCostList.add(
                Map.of(forwardingFirmName, convertToInteger(chargeExport.getDO_FEE().getLCL())));
            warfageCostList.add(
                Map.of(forwardingFirmName, convertToInteger(chargeExport.getWARFAGE_FEE().getLCL())));
        }

        Map<String, List<Map<String, Integer>>> compareCostMap = Map.of(
            "thcCost", thcCostList,
            "handlingCost", handlingCostList,
            "cfsCost", cfsCostList,
            "liftStatusCost", liftStatusCostList,
            "customsClearanceCost", customsClearanceCostList,
            "truckingCost", truckingCostList,
            "cicCost", cicCostList,
            "doFeeCost", dofeeCostList,
            "warfageCost", warfageCostList
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

        Map<Pair<String,String>, Integer> predictionList = new LinkedHashMap<>();
        for (Prediction prediction : predictions) {
            predictionList.put(
                Pair.of(
                    String.valueOf(prediction.getYear()),
                    String.format("%02d", prediction.getMonth())
                ),
                Integer.parseInt(prediction.getFreightCostIndex())
            );
        }


        Port exportPort = portRepository.findById(exportPortId)
            .orElseThrow(() -> new PortHandler(ErrorStatus.EXPORT_PORT_NOT_FOUND));
        Port importPort = portRepository.findById(importPortId)
            .orElseThrow(() -> new PortHandler(ErrorStatus.IMPORT_PORT_NOT_FOUND));

        return DashboardPredictionResponse.fromEntity(exportPort.getName(), importPort.getName(), predictionList);
    }

    // todo
    // API 호출 시간 문제
    public DashboardPredictionReasonResponse getPredictionReasonInfo() {
        LocalDate today = LocalDate.now();

        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        LocalDate sixMonthsLater = today.plusMonths(6);
        int endYear = sixMonthsLater.getYear();
        int endMonth = sixMonthsLater.getMonthValue();

        List<Prediction> predictions = predictionRepository.findPredictionsWithinPeriod(
            currentYear, currentMonth, endYear, endMonth);


        predictions.sort((p1, p2) -> {
            if (!Objects.equals(p1.getYear(), p2.getYear())) {
                return Integer.compare(p1.getYear(), p2.getYear());
            }
            return Integer.compare(p1.getMonth(), p2.getMonth());
        });

        List<PredictionReason> predictionReasons = IntStream.range(0, predictions.size() - 1)
            .mapToObj(i -> {
                Prediction current = predictions.get(i);
                Prediction next = predictions.get(i + 1);

                String status = Integer.parseInt(next.getFreightCostIndex()) > Integer.parseInt(current.getFreightCostIndex()) ? "rising" : "falling";

                Map<String, String> currentDate = Map.of(
                    "year", String.valueOf(current.getYear()),
                    "month", String.valueOf(current.getMonth())
                );
                Map<String, String> nextDate = Map.of(
                    "year", String.valueOf(next.getYear()),
                    "month", String.valueOf(next.getMonth())
                );

                String prompt = String.format(
                    "해운 운임 지수가 %s년 %s월부터 %s년 %s월 사이에 %s하고 있습니다. " +
                        "이전 월의 지수는 %s이고, 다음 월의 지수는 %s입니다. " +
                        "이러한 변화의 가능한 이유를 50단어 이내로 설명해주세요. " +
                        "국제 무역, 경제 상황, 연료 가격, 선박 공급량 등의 요인을 고려해 주세요.",
                    current.getYear(), current.getMonth(),
                    next.getYear(), next.getMonth(),
                    status.equals("rising") ? "상승" : "하락",
                    current.getFreightCostIndex(),
                    next.getFreightCostIndex()
                );

                ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                    .model("gpt-3.5-turbo")
                    .messages(List.of(
                        new ChatMessage("system", "You are a helpful assistant."),
                        new ChatMessage("user", prompt)
                    ))
                    .maxTokens(200)
                    .temperature(0.7)
                    .build();

                String reason = openAiService.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0).getMessage().getContent().trim();

                return PredictionReason.fromEntity(
                    List.of(currentDate, nextDate),
                    status,
                    reason
                );
            })
            .collect(Collectors.toList());

        return DashboardPredictionReasonResponse.fromEntity(predictionReasons);
    }

    public DashboardPortCongestionResponse getImportPortCongestion(Long importPortId) {
        Port importPort = portRepository.findById(importPortId)
            .orElseThrow(() -> new PortHandler(ErrorStatus.IMPORT_PORT_NOT_FOUND));

        String status = "혼잡";
        String description = "중국 국경절은 매년 10월 1일부터 7일까지로, 연휴가 끝난 10월 8일 이후부터 항구 운영이 재개됩니다. 이로 인해 연휴 후 혼잡도가 더욱 증가할 수 있습니다.";

        return DashboardPortCongestionResponse.fromEntity(status, description);
    }

    public DashboardNewsResponse getInterestingNews(List<String> interests) {
        LocalDate today = LocalDate.now();
        List<String> summaries = interests.stream()
            .flatMap(query -> newsRepository.findByCategoryAndCreatedDate(query, today).stream())
            .map(News::getContent)
            .toList();

        String content = String.join(" ", summaries);

//        String prompt = content + "다음 여러 영역의 기사 내용을 100자로 요약해주세요. 이때 제공되는 뉴스 요약본은 해운물류와 관련된 사용자가 "
//            + "도움을 얻을 수 있는 정보들입니다. ";
//        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
//            .model("gpt-3.5-turbo")
//            .messages(List.of(
//                new ChatMessage("system", "You are a helpful assistant."),
//                new ChatMessage("user", prompt)
//            ))
//            .maxTokens(200)
//            .temperature(0.7)
//            .build();
//
//        String summary = openAiService.createChatCompletion(chatCompletionRequest)
//            .getChoices().get(0).getMessage().getContent().trim();

        String summary = "국경절과 성수기 말기의 영향으로 상하이항 혼잡도는 평소보다 50%증가할 가능성이 큽니다. 환율은 현재와 유사하게 1,300원1,400원 사이에서 변동할 것으로 예상되며, 운임은 성수기 말기와 국경절 연휴 영향으로 현재 수준과 비슷하거나 다소 상승할 가능성이 있습니다.";

        return DashboardNewsResponse.fromEntity(interests, summary);

    }

    public DashboardRecommendationResponse getRecommendationInfoByCost(String rawQuotationId) {
        Quotation rawQuotation = quotationRepository.findQuotationById(rawQuotationId)
            .orElseThrow(()->new QuotationHandler(ErrorStatus.QUOTATION_NOT_FOUND));

        Cargo cargo = cargoRepository.findById(rawQuotation.getCost().getCargoIds().get(0))
            .orElseThrow(()-> new QuotationHandler(ErrorStatus.CARGO_NOT_FOUND));

        LocalDate today = LocalDate.from(cargo.getWishExportDate());

        int currentYear = today.getYear();
        int currentMonth = today.getMonthValue();

        LocalDate sixMonthsLater = today.plusMonths(6);
        int endYear = sixMonthsLater.getYear();
        int endMonth = sixMonthsLater.getMonthValue();

        List<Prediction> predictions = predictionRepository.findPredictionsWithinPeriod(
            currentYear, currentMonth, endYear, endMonth);

        Prediction todayMonthPrediction = predictionRepository.findByMonthAndYear(currentMonth, currentYear);

        Prediction minFreightCostPrediction = predictions.stream()
            .min(Comparator.comparingInt(p -> Integer.parseInt(p.getFreightCostIndex())))
            .get();

        // Prediction의 년도와 월을 이용해 YearMonth 객체 생성
        YearMonth predictionYearMonth = YearMonth.of(
            minFreightCostPrediction.getYear(),
            minFreightCostPrediction.getMonth()
        );

        YearMonth currentYearMonth = YearMonth.from(today);

        // 현재 날짜와 Prediction 날짜 사이의 기간 계산
        long monthsDifference = ChronoUnit.MONTHS.between(currentYearMonth, predictionYearMonth);
        System.out.println(minFreightCostPrediction.getFreightCostIndex());

        // 총 개월 수 계산
        Integer dateDifference = (int) monthsDifference;

        // 운임 비용 차이
        Integer indexDifference =
            Integer.parseInt(todayMonthPrediction.getFreightCostIndex()) - Integer.parseInt(
                minFreightCostPrediction.getFreightCostIndex());

        // 해당 화주가 요청한 견적서에 해당하는 알고리즘에 의해 계산된 견적서
        Quotation quotation
            = quotationRepository.findQuotationsByRawQuotationIdAndQuotationStatus(
                rawQuotationId,
                QuotationStatus.PREDICTION_SHEET
        ).get(0);

        // 알고리즘에 의한 견적서를 기반으로 비용 계산
        BigDecimal estimatedCost = quotationCalculationService.calculateTotalCost(quotation,
            Integer.valueOf(minFreightCostPrediction.getFreightCostIndex()));

        List<Schedule> schedules = scheduleRepository.findSchedulesByYearMonth(
            predictionYearMonth.getYear(), predictionYearMonth.getMonthValue());
        return DashboardRecommendationResponse.fromEntity(dateDifference, indexDifference,
            estimatedCost, schedules);
    }

    public String getAIReport(String rawQuotationId) {
        Quotation rawQuotation = quotationRepository.findQuotationById(rawQuotationId)
            .orElseThrow(() -> new QuotationHandler(ErrorStatus.QUOTATION_NOT_FOUND));

        // ChargeExport 관련 정보
        DashboardQuotationCompareResponse dashboardQuotationCompareResponse = getQuotationsForComparing(rawQuotationId);
        List<Map<String,Integer>> thcCostList = dashboardQuotationCompareResponse.thcCostList();
        List<Map<String,Integer>> cicCostList = dashboardQuotationCompareResponse.cicCostList();
        List<Map<String,Integer>> handlingCostList = dashboardQuotationCompareResponse.handlingCostList();
        List<Map<String,Integer>> cfsCostList = dashboardQuotationCompareResponse.cfsCostList();
        List<Map<String,Integer>> dofeeCostList = dashboardQuotationCompareResponse.dofeeCostList();
        List<Map<String,Integer>> warfeageCostList = dashboardQuotationCompareResponse.cfsCostList();

        String thcCostString = convertListToString(thcCostList);
        String cicCostString = convertListToString(cicCostList);
        String handlingCostString = convertListToString(handlingCostList);
        String cfsCostString = convertListToString(cfsCostList);
        String dofeeCostString = convertListToString(dofeeCostList);
        String warfeageCostString = convertListToString(warfeageCostList);

        // 화물 관련 정보
        List<String> cargoIdList = rawQuotation.getCost().getCargoIds();
        List<Cargo> cargos = cargoIdList.stream()
            .map(cargoId -> cargoRepository.findById(cargoId).orElseThrow(()-> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND)))
            .toList();

        List<CargoBaseInfo> cargoBaseInfos = quotationCalculationService.processCargos(cargos, 1320);
        List<String> promptCargoInfo = IntStream.range(0, cargos.size())
            .mapToObj(i -> String.format("Product: %s, Total CBM: %s",
                cargos.get(i).getCargoInfo().getProductName(),
                cargoBaseInfos.get(i).getTotalCBM().toString()))
            .toList();

        Cargo cargo = cargos.get(0);
        String incoterms = cargo.getIncoterms();

        // 스케줄 관련
        String today = String.valueOf(LocalDate.now());
        String ETD = String.valueOf(cargo.getWishExportDate());
        String exportPortName = portRepository.findById(cargo.getExportPortId()).orElseThrow(()->new PortHandler(ErrorStatus.EXPORT_PORT_NOT_FOUND)).getName();
        String importPortName = portRepository.findById(cargo.getImportPortId()).orElseThrow(()-> new PortHandler(ErrorStatus.IMPORT_PORT_NOT_FOUND)).getName();

        // 임시
        Integer freightCost = 900;

        String prompt = "나는 LCL수출 화주야. \n"
            + "\n"
            + "현재 날짜는 "+ today +" 이고," + ETD + "에 " + exportPortName + "에서 "+ importPortName+"으로 LCL수출을 진행하려고 해. 내가 준 정보를 바탕으로 LCL수출("+ETD+" 출항일 기준)에 도움이 될 BI 를 제공해줘.\n"
            + "\n"
            + "조건: 출항일을 기준으로 한 입항지, 출항지의 혼잡도와 항로별 운임예측 값, 인코텀즈, 수출 품목 등 제시된 여러 정보를 고려해야 함, 레포트 형식으로 작성해야함\n"
            + "\n"
            + "\n"
            + "정보1.\n"
            + "여러 포워더로부터 요청한 견적서를 받음. \n"
            + "(1) THC \n " + thcCostString
            + "(2) CIC \n " + cicCostString
            + "(3) DO FEE\n " + dofeeCostString
            + "(4) CFS\n " + cfsCostString
            + "(5) HANDLING FEE\n " + handlingCostString
            + "(6) WARFAGE FEE\n" + warfeageCostString
            + "정보2. 현재 운임: $"+freightCost+"\n"
            + "정보3." + promptCargoInfo + "인코텀즈"+ incoterms+"\n"
            + "\n"
            + "\n"
            + "초보화주의 입장에서 도움이 될만한 BI를 아래 양식에 맞춰서 보내줘. \n"
            + "\n"
            + "1. 추천하는 포워딩 업체\n"
            + "(1) 낮은 가격 기준\n"
            + "(2) 그 밖의 다른 요인\n"
            + "\n"
            + "2. 예측되는 더 저렴한 운임시기\n"
            + "(1) 날짜\n"
            + "(2) 운임\n"
            + "(3) 이유\n"
            + "\n"
            + "3. 참고하면 좋은 뉴스\n"
            + "\n"
            + "4. AI의 제안";


        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
            .model("gpt-3.5-turbo")  // gpt-4 모델 사용
            .messages(List.of(
                new ChatMessage("system", "You are a helpful assistant."),
                new ChatMessage("user", prompt)
            ))
            .maxTokens(2000)
            .temperature(0.7)
            .build();

        return openAiService.createChatCompletion(chatCompletionRequest)
            .getChoices().get(0).getMessage().getContent().trim();
    }
}
