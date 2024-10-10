package com.example.linkcargo.domain.dashboard.dto.response;

import com.example.linkcargo.domain.schedule.Schedule;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record DashboardRecommendationResponse(
    Integer dateDifference,
    Integer indexDifference,
    BigDecimal estimatedCost,
    List<ScheduleInfo> scheduleInfos

) {
    @Builder
    public record ScheduleInfo(
        String vesselName,
        LocalDate ETD,
        LocalDate ETA,
        Integer transitTime,
        String transportType,
        LocalDate documentCutOff,
        LocalDate cargoCutOff
    ) {
       public static ScheduleInfo fromEntity(Schedule schedule){
           return ScheduleInfo.builder()
               .vesselName(schedule.getVessel())
               .ETD(LocalDate.from(schedule.getETD()))
               .ETA(LocalDate.from(schedule.getETA()))
               .transitTime(schedule.getTransitTime())
               .transportType(String.valueOf(schedule.getTransportType()))
               .documentCutOff(LocalDate.from(schedule.getDocumentCutOff()))
               .cargoCutOff(LocalDate.from(schedule.getCargoCutOff()))
               .build();
       }
    }

    public static DashboardRecommendationResponse fromEntity(Integer dateDifference, Integer indexDifference, BigDecimal estimatedCost, List<Schedule> schedules) {
        List<ScheduleInfo> scheduleInfoList = schedules.stream()
            .map(ScheduleInfo::fromEntity)
            .toList();

        return DashboardRecommendationResponse.builder()
            .dateDifference(dateDifference)
            .indexDifference(indexDifference)
            .estimatedCost(estimatedCost)
            .scheduleInfos(scheduleInfoList)
            .build();
    }

}
