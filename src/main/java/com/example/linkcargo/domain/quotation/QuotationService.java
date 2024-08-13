package com.example.linkcargo.domain.quotation;

import com.example.linkcargo.domain.cargo.Cargo;
import com.example.linkcargo.domain.cargo.CargoRepository;
import com.example.linkcargo.domain.quotation.dto.request.QuotationConsignorRequest;
import com.example.linkcargo.domain.quotation.dto.request.QuotationForwarderRequest;
import com.example.linkcargo.domain.schedule.Schedule;
import com.example.linkcargo.domain.schedule.ScheduleRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.GeneralException;
import com.example.linkcargo.global.response.exception.handler.CargoHandler;
import com.example.linkcargo.global.response.exception.handler.GeneralHandler;
import com.example.linkcargo.global.response.exception.handler.QuotationHandler;
import com.example.linkcargo.global.response.exception.handler.ScheduleHandler;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final CargoRepository cargoRepository;
    private final ScheduleRepository scheduleRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${api-key.export-import-bok.secretKey}")
    private String apiKey;

    public String getExchange() {
        String url = "https://www.koreaexim.go.kr/site/program/financial/exchangeJSON" +
            "?authkey=" + apiKey +
            "&data=AP01";
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            JsonNode rootNode = objectMapper.readTree(response.getBody());
            for (JsonNode node : rootNode) {
                if ("미국 달러".equals(node.get("cur_nm").asText())) {
                    return node.get("kftc_bkpr").asText();
                }
            }
        } catch (Exception e) {
            throw new GeneralHandler(ErrorStatus.EXTERNAL_API_ERROR);
        }
        return null;
    }

    @Transactional
    public Quotation createQuotationByConsignor(QuotationConsignorRequest request, Long userId) {
        List<String> cargoIds = request.cargoIds();
        for (String cargoId : cargoIds) {
            boolean isDuplicate = quotationRepository.existsByConsignorIdAndFreight_ScheduleId(
                String.valueOf(userId),
                String.valueOf(request.scheduleId())
            );

            if (isDuplicate) {
                throw new GeneralException(ErrorStatus.QUOTATION_DUPLICATE);
            }
        }

// 모든 cargoId의 존재 여부 확인
        for (String cargoId : cargoIds) {
            cargoRepository.findById(cargoId)
                .orElseThrow(() -> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND));
        }

// Schedule 존재 여부 확인
        scheduleRepository.findById(request.scheduleId())
            .orElseThrow(() -> new ScheduleHandler(ErrorStatus.SCHEDULE_NOT_FOUND));

// Quotation 생성 (여러 cargoIds 포함)
        Quotation quotation = request.toEntity(String.valueOf(userId));
        quotation.prePersist();
        quotation.setQuotationStatus(QuotationStatus.BASIC_INFO);
        return quotationRepository.save(quotation);

    }


    @Transactional
    public List<Quotation> createQuotationsByConsignor(List<QuotationConsignorRequest> requests, Long userId) {

        return requests.stream()
            .map(request -> {
                List<String> cargoIds = request.cargoIds();

                // 모든 cargoId에 대해 중복 검사
                cargoIds.forEach(cargoId -> {
                    boolean isDuplicate = quotationRepository.existsByConsignorIdAndFreight_ScheduleId(
                        String.valueOf(userId),
                        String.valueOf(request.scheduleId())
                    );

                    if (isDuplicate) {
                        throw new GeneralException(ErrorStatus.QUOTATION_DUPLICATE);
                    }
                });

                // 모든 cargoId의 존재 여부 확인
                cargoIds.forEach(cargoId ->
                    cargoRepository.findById(cargoId)
                        .orElseThrow(() -> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND))
                );

                // Schedule 존재 여부 확인
                scheduleRepository.findById(request.scheduleId())
                    .orElseThrow(() -> new ScheduleHandler(ErrorStatus.SCHEDULE_NOT_FOUND));

                // 하나의 Quotation 생성 (여러 cargoIds 포함)
                Quotation quotation = request.toEntity(String.valueOf(userId));
                quotation.prePersist();
                quotation.setQuotationStatus(QuotationStatus.BASIC_INFO);
                return quotationRepository.save(quotation);
            })
            .collect(Collectors.toList());
    }

    @Transactional
    public String updateQuotationByForwarder(QuotationForwarderRequest request, Long userId) {
        Quotation quotation = quotationRepository.findById(request.quotationId())
            .orElseThrow(() -> new QuotationHandler(ErrorStatus.QUOTATION_NOT_FOUND));

        try {
            Quotation updatedQuotation = request.updateQuotation(quotation, String.valueOf(userId));
            updatedQuotation.setQuotationStatus(QuotationStatus.DETAIL_INFO);
            quotationRepository.save(updatedQuotation);
        } catch (Exception e) {
            throw new QuotationHandler(ErrorStatus.QUOTATION_UPDATED_FAIL);
        }

        return quotation.getId();

    }

    @Transactional
    public String updateQuotationByAlgorithm(Quotation inputQuotation) {
//        Quotation quotation = quotationRepository.findById(inputQuotation.getId())
//            .orElseThrow(() -> new QuotationHandler(ErrorStatus.QUOTATION_NOT_FOUND));
//
////        Cargo cargo = cargoRepository.findById(quotation.getCost().getCargoIds())
////            .orElseThrow(() -> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND));
//
//        Schedule schedule = scheduleRepository.findById(Long.valueOf(quotation.getFreight().getScheduleId()))
//            .orElseThrow(() -> new ScheduleHandler(ErrorStatus.SCHEDULE_NOT_FOUND));
//
//        // 화물정보
//        BigDecimal weight = cargo.getCargoInfo().getWeight();
//        Integer applied_exchange_rate = Integer.valueOf(getExchange());
//        BigDecimal value = cargo.getCargoInfo().getValue();

        return null;

    }
}
