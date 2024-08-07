package com.example.linkcargo.domain.quotation;

import com.example.linkcargo.domain.cargo.Cargo;
import com.example.linkcargo.domain.cargo.CargoRepository;
import com.example.linkcargo.domain.quotation.dto.request.QuotationConsignorRequest;
import com.example.linkcargo.domain.schedule.Schedule;
import com.example.linkcargo.domain.schedule.ScheduleRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.GeneralException;
import com.example.linkcargo.global.response.exception.handler.CargoHandler;
import com.example.linkcargo.global.response.exception.handler.ScheduleHandler;
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
    public String createQuotationByConsignor(QuotationConsignorRequest request, Long userId) {

        boolean isDuplicate = quotationRepository.existsByUserIdAndCost_CargoIdAndFreight_ScheduleId(
            String.valueOf(userId),
            request.cargoId(),
            String.valueOf(request.scheduleId())
        );

        if (isDuplicate) {
            throw new GeneralException(ErrorStatus.QUOTATION_DUPLICATE);
        }

        cargoRepository.findById(request.cargoId())
            .orElseThrow(() -> new CargoHandler(ErrorStatus.CARGO_NOT_FOUND));

        scheduleRepository.findById(request.scheduleId())
            .orElseThrow(() -> new ScheduleHandler(ErrorStatus.SCHEDULE_NOT_FOUND));


        Quotation quotation = request.toEntity(String.valueOf(userId));
        quotation.setQuotationStatus(QuotationStatus.BASIC_INFO);
        Quotation savedQuotation = quotationRepository.save(quotation);
        return savedQuotation.getId();

    }


}
