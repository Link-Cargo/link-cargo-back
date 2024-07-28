package com.example.linkcargo.domain.schedule;

import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.domain.port.PortRepository;
import com.example.linkcargo.domain.schedule.dto.request.ScheduleCreateUpdateRequest;
import com.example.linkcargo.domain.schedule.dto.response.ScheduleInfoResponse;
import com.example.linkcargo.domain.schedule.dto.response.ScheduleListResponse;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.PortHandler;
import com.example.linkcargo.global.response.exception.handler.ScheduleHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final PortRepository portRepository;

    @Transactional
    public Long createSchedule(ScheduleCreateUpdateRequest request) {

        // 이미 존재하는 스케줄인지 확인
        if (scheduleRepository.existsByCarrierAndETDAndETAAndTransportType(
                request.carrier(),
                request.ETD(),
                request.ETA(),
                request.transportType())) {
            throw new ScheduleHandler(ErrorStatus.SCHEDULE_ALREADY_EXISTS);
        }

        Port exportPort = portRepository.findById(request.exportPortId())
                .orElseThrow(() -> new PortHandler(ErrorStatus.EXPORT_PORT_NOT_FOUND));
        Port importPort = portRepository.findById(request.importPortId())
                .orElseThrow(() -> new PortHandler(ErrorStatus.IMPORT_PORT_NOT_FOUND));

        Schedule schedule = request.toEntity(exportPort, importPort);

        // 생성 중 예외 발생 시 처리
        try {
            Schedule savedSchedule = scheduleRepository.save(schedule);
            return savedSchedule.getId();
        } catch (Exception e) {
            throw new ScheduleHandler(ErrorStatus.SCHEDULE_CREATED_FAIL);
        }
    }

    public ScheduleInfoResponse findSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ScheduleHandler(ErrorStatus.SCHEDULE_NOT_FOUND));
        return ScheduleInfoResponse.fromEntity(schedule);
    }

    public ScheduleListResponse findSchedules(int page, int size) {
        Page<Schedule> schedulePage = scheduleRepository.findAll(PageRequest.of(page,size));
        return ScheduleListResponse.fromEntity(schedulePage);
    }

    @Transactional
    public void modifySchedule(Long scheduleId, ScheduleCreateUpdateRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId).orElseThrow(() -> new ScheduleHandler(ErrorStatus.SCHEDULE_NOT_FOUND));

        Port exportPort = portRepository.findById(request.exportPortId())
                .orElseThrow(() -> new PortHandler(ErrorStatus.EXPORT_PORT_NOT_FOUND));
        Port importPort = portRepository.findById(request.importPortId())
                .orElseThrow(() -> new PortHandler(ErrorStatus.IMPORT_PORT_NOT_FOUND));

        schedule.setExportPort(exportPort);
        schedule.setImportPort(importPort);
        schedule.setCarrier(request.carrier());
        schedule.setETD(request.ETD());
        schedule.setETA(request.ETA());
        schedule.setTransportType(request.transportType());
        schedule.setTransitTime(request.transitTime());
        schedule.setDocumentCutOff(request.documentCutOff());
        schedule.setCargoCutOff(request.cargoCutOff());

        try {
            scheduleRepository.save(schedule);
        } catch (Exception e) {
            throw new ScheduleHandler(ErrorStatus.SCHEDULE_UPDATED_FAIL);
        }
    }
}
