package com.example.linkcargo.domain.dashboard.dto.response;

import lombok.Builder;

@Builder
public record DashboardPortCongestionResponse(
    String status,
    String description
) {
    public static DashboardPortCongestionResponse fromEntity( String status,
        String description) {
        return DashboardPortCongestionResponse.builder()
            .status(status)
            .description(description)
            .build();
    }

}
