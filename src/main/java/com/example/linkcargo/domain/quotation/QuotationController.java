package com.example.linkcargo.domain.quotation;

import com.example.linkcargo.domain.quotation.dto.request.QuotationConsignorRequest;
import com.example.linkcargo.domain.schedule.dto.request.ScheduleCreateUpdateRequest;
import com.example.linkcargo.global.resolver.Login;
import com.example.linkcargo.global.resolver.LoginInfo;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "4. Quotation", description = "견적서 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/quotations")
public class QuotationController {

    private final QuotationService quotationService;

    @Operation(summary = "화주 견적서 요청 ", description = "화주 측에서 견적서 초안을 작성합니다. QuotationConsignorRequest 사용")
    @PostMapping("")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<String> createQuotationByConsignor(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @RequestBody QuotationConsignorRequest request) {
        String resultId = quotationService.createQuotationByConsignor(request, userDetail.getId());
        return ApiResponse.onSuccess(resultId);
    }
}
