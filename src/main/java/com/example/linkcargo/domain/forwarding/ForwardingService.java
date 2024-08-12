package com.example.linkcargo.domain.forwarding;

import com.example.linkcargo.domain.forwarding.dto.request.ForwardingCreateUpdateRequest;
import com.example.linkcargo.domain.forwarding.dto.response.ForwardingInfoResponse;
import com.example.linkcargo.domain.schedule.dto.request.ScheduleCreateUpdateRequest;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.ForwardingHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ForwardingService {

    private final ForwardingRepository forwardingRepository;

    @Transactional
    public Long createForwarding(ForwardingCreateUpdateRequest request) {
        boolean checkDuplicate = forwardingRepository.existsByFirmName(request.firmName());
        if (checkDuplicate) {
            throw new ForwardingHandler(ErrorStatus.FORWARDING_ALREADY_EXISTS);
        }

        Forwarding forwarding = request.toEntity();

        try {
            Forwarding createdForwarding = forwardingRepository.save(forwarding);
            return createdForwarding.getId();
        } catch (Exception e) {
            throw new ForwardingHandler(ErrorStatus.FORWARDING_CREATED_FAIL);
        }
    }

    public ForwardingInfoResponse findForwarding(Long forwardingId) {
        Forwarding forwarding = forwardingRepository.findById(forwardingId)
            .orElseThrow(()-> new ForwardingHandler(ErrorStatus.FORWARDING_NOT_FOUND));

        return ForwardingInfoResponse.fromEntity(forwarding);
    }

    @Transactional
    public void modifyForwarding(Long forwardingId, ForwardingCreateUpdateRequest request) {
        Forwarding forwarding = forwardingRepository.findById(forwardingId)
            .orElseThrow(()-> new ForwardingHandler(ErrorStatus.FORWARDING_NOT_FOUND));

        try {
            Forwarding updatedForwarding = request.updateEntity(forwarding);
            forwardingRepository.save(updatedForwarding);
        } catch (Exception e) {
            throw new ForwardingHandler(ErrorStatus.FORWARDING_UPDATED_FAIL);
        }


    }
}
