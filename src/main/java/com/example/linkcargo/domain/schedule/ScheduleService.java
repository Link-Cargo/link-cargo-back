package com.example.linkcargo.domain.schedule;

import com.example.linkcargo.domain.dashboard.dto.response.DashboardRecommendationResponse.ScheduleInfo;
import com.example.linkcargo.domain.image.ImageService;
import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.domain.port.PortRepository;
import com.example.linkcargo.domain.schedule.dto.request.ScheduleCreateUpdateRequest;
import com.example.linkcargo.domain.schedule.dto.response.ScheduleInfoResponse;
import com.example.linkcargo.domain.schedule.dto.response.ScheduleListResponse;
import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.UserRepository;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.PortHandler;
import com.example.linkcargo.global.response.exception.handler.ScheduleHandler;
import com.example.linkcargo.global.response.exception.handler.UsersHandler;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final PortRepository portRepository;
    private final ImageService imageService;
    private final UserRepository userRepository;

    @Transactional
    public Long createSchedule(ScheduleCreateUpdateRequest request, Long userId) {

        // 이미 존재하는 스케줄인지 확인
        if (scheduleRepository.existsByCarrierAndETDAndETAAndTransportType(
            request.carrier(),
            request.ETD(),
            request.ETA(),
            request.transportType())) {
            throw new ScheduleHandler(ErrorStatus.SCHEDULE_ALREADY_EXISTS);
        }

        User forwarder = userRepository.findById(userId).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));

        Port exportPort = portRepository.findById(request.exportPortId())
            .orElseThrow(() -> new PortHandler(ErrorStatus.EXPORT_PORT_NOT_FOUND));
        Port importPort = portRepository.findById(request.importPortId())
            .orElseThrow(() -> new PortHandler(ErrorStatus.IMPORT_PORT_NOT_FOUND));

        Schedule schedule = request.toEntity(exportPort, importPort, forwarder);

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
        return ScheduleInfoResponse.fromEntity(schedule,"");
    }

    public ScheduleListResponse findSchedules(int page, int size) {
        Page<Schedule> schedulePage = scheduleRepository.findAll(PageRequest.of(page,size));
        List<String> imageUrls = imageService.selectRandomImages("vessel",schedulePage.getSize());
        return ScheduleListResponse.fromEntity(schedulePage,imageUrls);
    }

    @Transactional
    public void modifySchedule(Long scheduleId, ScheduleCreateUpdateRequest request, Long userId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ScheduleHandler(ErrorStatus.SCHEDULE_NOT_FOUND));

        User forwarder = userRepository.findById(userId).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));


        Port exportPort = portRepository.findById(request.exportPortId())
            .orElseThrow(() -> new PortHandler(ErrorStatus.EXPORT_PORT_NOT_FOUND));
        Port importPort = portRepository.findById(request.importPortId())
            .orElseThrow(() -> new PortHandler(ErrorStatus.IMPORT_PORT_NOT_FOUND));

        Schedule updatedSchedule = request.updateEntity(schedule,exportPort,importPort,forwarder);

        try {
            scheduleRepository.save(updatedSchedule);
        } catch (Exception e) {
            throw new ScheduleHandler(ErrorStatus.SCHEDULE_UPDATED_FAIL);
        }
    }

    @Transactional
    public void removeSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
            .orElseThrow(() -> new ScheduleHandler(ErrorStatus.SCHEDULE_NOT_FOUND));

        try {
            scheduleRepository.delete(schedule);
        } catch (Exception e) {
            throw new ScheduleHandler(ErrorStatus.SCHEDULE_UPDATED_FAIL);
        }
    }

    public ScheduleListResponse searchSchedules(Long exportPortId, Long importPortId, Double inputCBM, LocalDate searchDate, int page, int size) {
        LocalDate startDate = searchDate.withDayOfMonth(1);
        LocalDate endDate = searchDate.withDayOfMonth(searchDate.lengthOfMonth());

        LocalDateTime startOfMonth = startDate.atStartOfDay();
        LocalDateTime endOfMonth = endDate.atTime(23, 59, 59);
        Pageable pageable = PageRequest.of(page, size, Sort.by("ETD").ascending());

        Integer limitCBM = null;

        if (inputCBM <= 28) {
            limitCBM = 28;
        } else if (inputCBM > 28 && inputCBM <= 48) {
            limitCBM = 48;
        }

        Page<Schedule> schedulesPage = scheduleRepository.findByExportPortIdAndImportPortIdAndETDBetweenAndLimitCBM(
            exportPortId, importPortId,startOfMonth, endOfMonth,  limitCBM, pageable
        );
        List<String> imageUrls = imageService.selectRandomImages("vessel",schedulesPage.getSize());

        return ScheduleListResponse.fromEntity(schedulesPage, imageUrls);
    }

    public List<ScheduleInfoResponse> findSchedulesByForwarderId(Long forwarderId) {
        User forwarder = userRepository.findById(forwarderId).orElseThrow(() -> new UsersHandler(ErrorStatus.USER_NOT_FOUND));
        List<Schedule> schedules = scheduleRepository.findSchedulesByForwarder(forwarder);

        return schedules.stream()
            .map(schedule -> ScheduleInfoResponse.fromEntity(schedule, ""))
            .collect(Collectors.toList());
    }
}
