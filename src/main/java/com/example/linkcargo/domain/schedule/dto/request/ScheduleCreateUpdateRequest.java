package com.example.linkcargo.domain.schedule.dto.request;


import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.domain.schedule.Schedule;
import com.example.linkcargo.domain.schedule.TransportType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleCreateUpdateRequest(
        @NotNull(message = "Export port is mandatory")
        Long exportPortId,

        @NotNull(message = "Import port is mandatory")
        Long importPortId,

        @NotBlank(message = "Carrier is mandatory")
        String carrier,

        @NotBlank(message = "Vessel is mandatory")
        String vessel,

        @NotNull(message = "ETD is mandatory")
        LocalDateTime ETD,

        @NotNull(message = "ETA is mandatory")
        LocalDateTime ETA,

        @NotNull(message = "Transport type is mandatory")
        TransportType transportType,

        @NotNull(message = "Transit time is mandatory")
        @Min(value = 1, message = "Transit time should be at least 1")
        Integer transitTime,

        @NotNull(message = "Document cut off is mandatory")
        LocalDateTime documentCutOff,

        @NotNull(message = "Cargo cut off is mandatory")
        LocalDateTime cargoCutOff
) {
    public Schedule toEntity(Port exportPort, Port importPort) {
        return Schedule.builder()
                .exportPort(exportPort)
                .importPort(importPort)
                .carrier(this.carrier)
                .vessel(this.vessel)
                .ETD(this.ETD)
                .ETA(this.ETA)
                .transportType(this.transportType)
                .transitTime(this.transitTime)
                .documentCutOff(this.documentCutOff)
                .cargoCutOff(this.cargoCutOff)
                .build();
    }
}