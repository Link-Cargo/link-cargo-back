package com.example.linkcargo.domain.prediction;

import com.example.linkcargo.domain.prediction.dto.request.PredictionCreateRequest;
import com.example.linkcargo.domain.schedule.dto.request.ScheduleCreateUpdateRequest;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.response.code.resultCode.SuccessStatus;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "13. Prediction", description = "운임지수 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/prediction")
public class PredictionController {

    private final PredictionService predictionService;

    @Operation(summary = "운임지수 생성", description = "운임지수를 AI 모델을 통해 생성 합니다. ScheduleCreateUpdateRequest 사용")
    @PostMapping("")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<SuccessStatus> createPrediction(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @RequestBody PredictionCreateRequest request) {
        predictionService.createPrediction(request);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}
