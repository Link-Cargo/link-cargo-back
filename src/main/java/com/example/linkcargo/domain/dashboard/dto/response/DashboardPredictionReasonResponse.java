package com.example.linkcargo.domain.dashboard.dto.response;

import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record DashboardPredictionReasonResponse(
    List<PredictionReason> predictionReasons
) {
    @Builder
    public record PredictionReason (
    List<Map<String,String>> date,
    String status,
    String reason
    ) {
        public static PredictionReason fromEntity(List<Map<String,String>> date, String status, String reason){
            return PredictionReason.builder()
                .date(date)
                .status(status)
                .reason(reason)
                .build();
        }
        }
    public static DashboardPredictionReasonResponse fromEntity (List<PredictionReason> predictionReasons) {
        return DashboardPredictionReasonResponse.builder()
            .predictionReasons(predictionReasons)
            .build();
    }

}
