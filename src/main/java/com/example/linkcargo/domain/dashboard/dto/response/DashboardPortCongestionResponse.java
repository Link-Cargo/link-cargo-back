package com.example.linkcargo.domain.dashboard.dto.response;

import lombok.Builder;

@Builder
public record DashboardPortCongestionResponse(
    Integer percent,
    String status,
    String description
) {
    public static DashboardPortCongestionResponse fromEntity(Integer percent, String status,
        String description) {
        return DashboardPortCongestionResponse.builder()
            .percent(percent)
            .status(status)
            .description(description)
            .build();
    }

}
