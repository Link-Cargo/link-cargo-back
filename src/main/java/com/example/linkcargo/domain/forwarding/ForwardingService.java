package com.example.linkcargo.domain.forwarding;

import com.example.linkcargo.domain.forwarding.dto.request.ForwardingCreateUpdateRequest;
import com.example.linkcargo.domain.forwarding.dto.response.ForwardingInfoResponse;
import com.example.linkcargo.domain.schedule.dto.request.ScheduleCreateUpdateRequest;
import com.example.linkcargo.domain.user.Role;
import com.example.linkcargo.domain.user.Status;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.ForwardingHandler;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
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
    private final UserRepository userRepository;

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

    @Transactional
    public void removeForwarding(Long forwardingId) {
        Forwarding forwarding = forwardingRepository.findById(forwardingId)
            .orElseThrow(()-> new ForwardingHandler(ErrorStatus.FORWARDING_NOT_FOUND));

        try {
            forwardingRepository.delete(forwarding);
        } catch (Exception e) {
            throw new ForwardingHandler(ErrorStatus.FORWARDING_UPDATED_FAIL);
        }
    }

    @Transactional
    public void selectForwarding(Long forwardingId, Long userId) {
        Forwarding forwarding = forwardingRepository.findById(forwardingId)
            .orElseThrow(()-> new ForwardingHandler(ErrorStatus.FORWARDING_NOT_FOUND));

        User user = userRepository.findById(userId)
            .orElseThrow(()-> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        if (user.getRole() == Role.CONSIGNOR) {
            throw new ForwardingHandler(ErrorStatus.NOT_FORWARDER);
        }

        user.setForwarding(forwarding);
        userRepository.save(user);
    }
}
