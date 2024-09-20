package com.example.linkcargo.domain.quotation.dto.response;

import com.example.linkcargo.domain.schedule.Schedule;
import java.time.LocalDate;
import java.util.List;
import lombok.Builder;

@Builder
public record EstimatedQuotationResponse(
    List<EstimatedQuotation> estimatedQuotations,
    Integer count

) {
    @Builder
    public record EstimatedQuotation(
        String carrier,
        LocalDate ETD,
        LocalDate ETA,
        String forwardingName
    ) {
        public static EstimatedQuotation fromEntity(Schedule schedule, String forwardingName) {
            return EstimatedQuotation.builder()
                .carrier(schedule.getCarrier())
                .ETD(LocalDate.from(schedule.getETD()))
                .ETA(LocalDate.from(schedule.getETA()))
                .forwardingName(forwardingName)
                .build();
        }
    }

    public static EstimatedQuotationResponse fromEntity(List<EstimatedQuotation> estimatedQuotations, Integer count) {
        return EstimatedQuotationResponse.builder()
            .estimatedQuotations(estimatedQuotations)
            .count(count)
            .build();
    }

}
