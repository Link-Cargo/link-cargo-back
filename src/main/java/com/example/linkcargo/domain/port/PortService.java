package com.example.linkcargo.domain.port;

import com.example.linkcargo.domain.port.dto.request.PortCreateUpdateRequest;
import com.example.linkcargo.domain.port.dto.response.PortReadResponse;
import com.example.linkcargo.domain.schedule.PortType;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.PortHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortService {

    private final PortRepository portRepository;

    @Transactional
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

    public List<PortReadResponse> findPorts() {
        List<Port> ports = portRepository.findAll();

        return ports.stream()
                .map(PortReadResponse::fromEntity)
                .toList();
    }

    @Transactional
    public void modifyPort(Long portId, PortCreateUpdateRequest request) {
        Port existingPort = portRepository.findById(portId)
                .orElseThrow(() -> new PortHandler(ErrorStatus.PORT_NOT_FOUND));

        if (portRepository.existsByNameAndIdNot(request.name(), portId)) {
            throw new PortHandler(ErrorStatus.PORT_ALREADY_EXISTS);
        }

        existingPort.setName(request.name());
        existingPort.setType(PortType.valueOf(request.type()));

        try {
            portRepository.save(existingPort);
        } catch (Exception e) {
            throw new PortHandler(ErrorStatus.PORT_UPDATED_FAIL);
        }
    }

    @Transactional
    public void removePort(Long portId) {
        Port existingPort = portRepository.findById(portId)
                .orElseThrow(() -> new PortHandler(ErrorStatus.PORT_NOT_FOUND));

        try {
            portRepository.delete(existingPort);
        } catch (Exception e) {
            throw new PortHandler(ErrorStatus.PORT_DELETED_FAIL);
        }
    }
}
