package com.example.linkcargo.domain.schedule.dto.request;


import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.domain.schedule.Schedule;
import com.example.linkcargo.domain.schedule.TransportType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotNull(message = "Limit size is mandatory")
    @Pattern(regexp = "20|40", message = "limitSize must be 20 or 40")
    Integer limitSize,

    @NotNull(message = "Qty is mandatory")
    Integer Qty,

    @NotNull(message = "Document cut off is mandatory")
    LocalDateTime documentCutOff,

    @NotNull(message = "Cargo cut off is mandatory")
    LocalDateTime cargoCutOff
) {

    public Schedule toEntity(Port exportPort, Port importPort) {
        Integer CBM = null;

        if (this.limitSize == 20) {
            CBM = 28;
        } else {
            CBM = 48;
        }

        return Schedule.builder()
            .exportPort(exportPort)
            .importPort(importPort)
            .carrier(this.carrier)
            .vessel(this.vessel)
            .ETD(this.ETD)
            .ETA(this.ETA)
            .limitSize(this.limitSize)
            .Qty(this.Qty)
            .limitCBM(CBM)
            .transportType(this.transportType)
            .transitTime(this.transitTime)
            .documentCutOff(this.documentCutOff)
            .cargoCutOff(this.cargoCutOff)
            .build();
    }

    public Schedule updateEntity(Schedule existingSchedule, Port exportPort, Port importPort) {
        existingSchedule.setExportPort(exportPort);
        existingSchedule.setImportPort(importPort);
        existingSchedule.setCarrier(this.carrier());
        existingSchedule.setVessel(this.vessel());
        existingSchedule.setETD(this.ETD());
        existingSchedule.setETA(this.ETA());
        existingSchedule.setTransportType(this.transportType());
        existingSchedule.setTransitTime(this.transitTime());
        existingSchedule.setLimitSize(this.limitSize());
        existingSchedule.setQty(this.Qty());
        existingSchedule.setDocumentCutOff(this.documentCutOff());
        existingSchedule.setCargoCutOff(this.cargoCutOff());

        return existingSchedule;
    }
}