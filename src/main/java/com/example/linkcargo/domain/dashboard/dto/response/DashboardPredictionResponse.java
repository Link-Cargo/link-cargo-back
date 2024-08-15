package com.example.linkcargo.domain.dashboard.dto.response;

import java.util.Map;
import lombok.Builder;
import org.apache.commons.lang3.tuple.Pair;

@Builder
public record DashboardPredictionResponse(

    Map<Pair<String,String>,Integer> predictions,
    String exportPortName,
    String importPortName
    ) {

    public static DashboardPredictionResponse fromEntity(
        String exportPortName,
        String importPortName,
        Map<Pair<String,String>,Integer> predictions
        ) {

        return DashboardPredictionResponse.builder()
            .exportPortName(exportPortName)
            .importPortName(importPortName)
            .predictions(predictions)
            .build();
    }

}
