package com.example.linkcargo.domain.forwarding;

import com.example.linkcargo.domain.forwarding.dto.request.ForwardingCreateUpdateRequest;
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
}
