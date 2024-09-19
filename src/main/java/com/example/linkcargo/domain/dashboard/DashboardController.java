package com.example.linkcargo.domain.dashboard;

import com.example.linkcargo.domain.dashboard.dto.response.DashboardNewsResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardPortCongestionResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardPredictionReasonResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardPredictionResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardQuotationCompareResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardQuotationResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardRawQuotationResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardRecommendationResponse;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "9. Dashboard", description = "대시보드 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboards")
public class DashboardController {

    private final DashboardService dashboardService;


    @Operation(summary = "유저 원시 견적서 조회", description = "유저의 원시 견적서를 조회합니다. DashboardRawQuotationResponse 사용")
    @GetMapping("/list")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403",description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER403", description = "해당 ID 의 유저가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUOTATION402", description = "해당 견적서가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<DashboardRawQuotationResponse> getRawQuotations(
        @AuthenticationPrincipal CustomUserDetail userDetail)
    {
        return ApiResponse.onSuccess(dashboardService.getRawQuotations(userDetail.getId()));
    }

    @Operation(summary = "가장 싼 견적서 조회", description = "요청한 견적서를 포워더가 업데이트 한 후 운임비용이 가장 적은 견적서를 조회합니다. DashboardQuotationResponse 사용")
    @GetMapping("/cheapest")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403",description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER403", description = "해당 ID 의 유저가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUOTATION402", description = "해당 견적서가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<DashboardQuotationResponse> getTheCheapestQuotation(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "화주가 요청한 견적서의 아이디") @RequestParam String quotationId)
     {
        return ApiResponse.onSuccess(dashboardService.getTheCheapestQuotation(quotationId));
    }

    @Operation(summary = "견적서 비교 ", description = "요청한 견적서를 포워더가 업데이트 한 후 견적서 끼리 비교합니다. DashboardQuotationCompareResponse 사용")
    @GetMapping("/compare")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403",description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "USER403", description = "해당 ID 의 유저가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<DashboardQuotationCompareResponse> getQuotationsForComparing(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "화주가 요청한 견적서의 아이디") @RequestParam String quotationId) {
        {
            return ApiResponse.onSuccess(
                dashboardService.getQuotationsForComparing(quotationId));
        }
    }

    @Operation(summary = "운임 비용 관련 그래프 정보 조회 ", description = "운임 비용 관련 그래프 정보를 조회합니다. DashboardPredictionResponse 사용")
    @GetMapping("/prediction")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<DashboardPredictionResponse> getPredictionInfo(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "수출항 ID") @RequestParam Long exportPortId,
        @Parameter(description = "수입항 ID") @RequestParam Long importPortId)
        {
            return ApiResponse.onSuccess(dashboardService.getPredictionInfo(exportPortId,importPortId));
        }


    @Operation(summary = "운임 비용 관련 이유 정보 조회 ", description = "운임 비용 관련 이유 정보를 조회합니다. DashboardPredictionReasonResponse 사용")
    @GetMapping("/prediction/reason")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<DashboardPredictionReasonResponse> getPredictionReasonInfo(
        @AuthenticationPrincipal CustomUserDetail userDetail)
    {
        return ApiResponse.onSuccess(dashboardService.getPredictionReasonInfo());
    }

    @Operation(summary = "입국항 혼잡도 정보 조회", description = "입국항의 혼잡도 정보를 조회합니다. DashboardPortCongestionResponse 사용")
    @GetMapping("/port/congestion")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<DashboardPortCongestionResponse> getImportPortCongestion(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "수입항 ID") @RequestParam Long importPortId)
    {
        return ApiResponse.onSuccess(dashboardService.getImportPortCongestion(importPortId));
    }

    @Operation(summary = "뉴스 요약 정보 조회", description = "사용자의 관심사에 따른 뉴스 정보의 요약 정보를 조회합니다. DashboardNewsResponse 사용")
    @GetMapping("/news/summary")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<DashboardNewsResponse> getInterestingNews(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "사용자가 선택한 관심사") @RequestParam List<String> interests)
    {
        return ApiResponse.onSuccess(dashboardService.getInterestingNews(interests));
    }

    @Operation(summary = "더 저렴한 가격 추천 정보 조회", description = "현재 달을 기준으로 6개월 동안의 운임비용이 가장 적을때의 선박 스케줄과 예상 비용 정보를 조회 합니다."
        + " DashboardRecommendationResponse 사용")
    @GetMapping("/recommendation")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<DashboardRecommendationResponse> getRecommendationInfoByCost(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "화주가 요청한 견적서의 아이디") @RequestParam String quotationId)
    {
        return ApiResponse.onSuccess(dashboardService.getRecommendationInfoByCost(quotationId));
    }



}
