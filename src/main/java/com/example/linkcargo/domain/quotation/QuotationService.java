package com.example.linkcargo.domain.quotation;

import com.example.linkcargo.domain.cargo.CargoRepository;
import com.example.linkcargo.domain.quotation.dto.request.QuotationConsignorRequest;
import com.example.linkcargo.domain.quotation.dto.request.QuotationForwarderRequest;
import com.example.linkcargo.domain.schedule.ScheduleRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.GeneralException;
import com.example.linkcargo.global.response.exception.handler.CargoHandler;
import com.example.linkcargo.global.response.exception.handler.QuotationHandler;
import com.example.linkcargo.global.response.exception.handler.ScheduleHandler;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QuotationService {

    private final QuotationRepository quotationRepository;
    private final CargoRepository cargoRepository;
    private final ScheduleRepository scheduleRepository;




    @Transactional
    public Quotation createQuotationByConsignor(QuotationConsignorRequest request, Long userId) {
        List<String> cargoIds = request.cargoIds();

        boolean isDuplicate = quotationRepository.existsByConsignorIdAndFreight_ScheduleId(
            String.valueOf(userId),
            String.valueOf(request.scheduleId()));


        if (isDuplicate) {
            throw new GeneralException(ErrorStatus.QUOTATION_DUPLICATE);
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
            Quotation newQuotation = request.updateQuotation(quotation, String.valueOf(userId));
            newQuotation.setQuotationStatus(QuotationStatus.DETAIL_INFO);
            newQuotation.setId(null);
            Quotation savedQuotation = quotationRepository.save(newQuotation);
            return savedQuotation.getId();
        } catch (Exception e) {
            throw new QuotationHandler(ErrorStatus.QUOTATION_UPDATED_FAIL);
        }

    }


}
