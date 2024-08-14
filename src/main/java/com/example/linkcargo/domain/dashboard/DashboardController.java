package com.example.linkcargo.domain.dashboard;

import com.example.linkcargo.domain.dashboard.dto.response.DashboardQuotationCompareResponse;
import com.example.linkcargo.domain.dashboard.dto.response.DashboardQuotationResponse;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "9. Dashboard", description = "대시보드 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/dashboards")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("")
    public ApiResponse<DashboardQuotationCompareResponse> test(
        @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        return ApiResponse.onSuccess(dashboardService.getQuotationsForComparing(userDetail.getId(),
            String.valueOf(1)));
    }
}
