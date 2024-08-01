package com.example.linkcargo.domain.schedule.dto.response;

import com.example.linkcargo.domain.schedule.Schedule;
import lombok.Builder;
import org.springframework.data.domain.Page;

import java.util.List;

@Builder
public record ScheduleListResponse(
        List<ScheduleInfoResponse> schedules,
        int currentPage,
        int totalPages,
        long totalElements
) {
    public static ScheduleListResponse fromEntity(Page<Schedule> schedulePage) {
        List<ScheduleInfoResponse> scheduleResponses = schedulePage.getContent().stream()
                .map(ScheduleInfoResponse::fromEntity)
                .toList();

        return ScheduleListResponse.builder()
                .schedules(scheduleResponses)
                .currentPage(schedulePage.getNumber())
                .totalPages(schedulePage.getTotalPages())
                .totalElements(schedulePage.getTotalElements())
                .build();
    }
}
