package com.example.linkcargo.domain.schedule.dto.response;

import com.example.linkcargo.domain.schedule.Schedule;
import com.example.linkcargo.domain.schedule.TransportType;

import java.time.LocalDateTime;

public record ScheduleInfoResponse(
        Long id,
        Long exportPortId,
        Long importPortId,
        String carrier,
        LocalDateTime ETD,
        LocalDateTime ETA,
        TransportType transportType,
        Integer transitTime,
        LocalDateTime documentCutOff,
        LocalDateTime cargoCutOff
) {
    public static ScheduleInfoResponse fromEntity(Schedule schedule) {
        return new ScheduleInfoResponse(
                schedule.getId(),
                schedule.getExportPort().getId(),
                schedule.getImportPort().getId(),
                schedule.getCarrier(),
                schedule.getETD(),
                schedule.getETA(),
                schedule.getTransportType(),
                schedule.getTransitTime(),
                schedule.getDocumentCutOff(),
                schedule.getCargoCutOff()
        );
    }
}
