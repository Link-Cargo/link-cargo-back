package com.example.linkcargo.domain.dashboard.dto.response;

import java.util.List;
import lombok.Builder;

@Builder
public record DashboardNewsResponse(
    List<String> interests,
    String summary

) {

    public static DashboardNewsResponse fromEntity(List<String> interests, String summary) {
        return DashboardNewsResponse.builder()
            .interests(interests)
            .summary(summary)
            .build();
    }

}
