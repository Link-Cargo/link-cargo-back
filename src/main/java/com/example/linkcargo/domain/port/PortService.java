package com.example.linkcargo.domain.port;

import com.example.linkcargo.domain.port.dto.request.PortCreateUpdateRequest;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.PortHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortService {

    private final PortRepository portRepository;
    public Long createPort(PortCreateUpdateRequest request) {
        // 같은 이름의 항구가 존재하는지 확인
        if (portRepository.existsByName(request.name())) {
            throw new PortHandler(ErrorStatus.PORT_ALREADY_EXISTS);
        }

        Port port = request.toEntity();

        try {
            Port resultPort = portRepository.save(port);
            return resultPort.getId();
        } catch (Exception e){
            throw new PortHandler(ErrorStatus.PORT_CREATED_FAIL);
        }
    }
}
