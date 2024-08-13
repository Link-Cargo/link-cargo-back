package com.example.linkcargo.domain.dashboard;

import com.example.linkcargo.domain.cargo.CargoRepository;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardQuotationResponse;
import com.example.linkcargo.domain.forwarding.ForwardingRepository;
import com.example.linkcargo.domain.quotation.Quotation;
import com.example.linkcargo.domain.quotation.QuotationRepository;
import com.example.linkcargo.domain.quotation.dto.response.QuotationInfoResponse;
import com.example.linkcargo.domain.schedule.Schedule;
import com.example.linkcargo.domain.schedule.ScheduleRepository;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.QuotationHandler;
import com.example.linkcargo.global.response.exception.handler.ScheduleHandler;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}
