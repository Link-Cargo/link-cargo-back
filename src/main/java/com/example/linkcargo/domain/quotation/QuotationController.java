package com.example.linkcargo.domain.quotation;

import com.example.linkcargo.domain.quotation.dto.request.QuotationConsignorRequest;
import com.example.linkcargo.domain.quotation.dto.request.QuotationForwarderRequest;
import com.example.linkcargo.domain.quotation.dto.response.QuotationInfoResponse;
import com.example.linkcargo.domain.schedule.dto.request.ScheduleCreateUpdateRequest;
import com.example.linkcargo.global.resolver.Login;
import com.example.linkcargo.global.resolver.LoginInfo;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quotations")
@Tag(name = "4. Quotation", description = "견적서 관련 API")
public class QuotationController {

    private final QuotationService quotationService;
    private final QuotationCalculationService quotationCalculationService;

    @Operation(summary = "화주 견적서 요청 ", description = "화주 측에서 견적서 초안을 작성합니다. QuotationConsignorRequest 사용")
    @PostMapping("")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403",description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CARGO402", description = "해당 ID 의 CARGO 가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUOTATION401", description = "이미 동일한 견적서가 존재합니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<String> createQuotationByConsignor(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @RequestBody QuotationConsignorRequest request) {
        Quotation quotation = quotationService.createQuotationByConsignor(request, userDetail.getId());
        quotationCalculationService.updateQuotationByAlgorithm(quotation);
        return ApiResponse.onSuccess(quotation.getId());
    }

    @Operation(summary = "화주 견적서 여러 개 요청", description = "화주 측에서 여러 개의 견적서 초안을 작성합니다. QuotationConsignorRequest 사용")
    @PostMapping("/bulk")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403",description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CARGO402", description = "해당 ID 의 CARGO 가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUOTATION401", description = "이미 동일한 견적서가 존재합니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<List<String>> createQuotationsByConsignor(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @RequestBody List<QuotationConsignorRequest> requests) {
        List<Quotation> quotations = quotationService.createQuotationsByConsignor(requests, userDetail.getId());

        List<String> quotationIds = new ArrayList<>();
        for (Quotation quotation : quotations) {
            quotationCalculationService.updateQuotationByAlgorithm(quotation);
            quotationIds.add(quotation.getId());
        }
        return ApiResponse.onSuccess(quotationIds);

    }

    @Operation(summary = "포워더 견적서 업데이트", description = "포워더 측에서 기존 견적서를 업데이트합니다. QuotationForwarderRequest 사용")
    @PutMapping("/update")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUOTATION402", description = "견적서가 존재하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "QUOTATION403", description = "견적서 업데이트에 실패했습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<String> updateQuotationByForwarder(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @RequestBody QuotationForwarderRequest request) {

        String updatedQuotationId = quotationService.updateQuotationByForwarder(request, userDetail.getId());
        return ApiResponse.onSuccess(updatedQuotationId);

    }

    @Operation(summary = "견적서 검색", description = "화주가 작성한 견적서와 알고리즘에 의해 만들어진 견적서, 포워더가 업데이트한 견적서를 조회합니다. QuotationInfoResponse 사용")
    @GetMapping("/search")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403",description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<List<QuotationInfoResponse>> findQuotationsByQuotationId(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "화주가 요청한 견적서의 아이디") @RequestParam String quotationId) {

        List<QuotationInfoResponse> quotationInfoResponses  = quotationService.findQuotationsByQuotationId(quotationId);
        return ApiResponse.onSuccess(quotationInfoResponses);

    }

    @Operation(summary = "화주의 견적서 조회", description = "화주가 작성한 견적서만 조회합니다. QuotationInfoResponse 사용")
    @GetMapping("/me")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403",description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<List<QuotationInfoResponse>> findQuotationsByConsignorId(
        @AuthenticationPrincipal CustomUserDetail userDetail) {

        List<QuotationInfoResponse> quotationInfoResponses  = quotationService.findQuotationsByConsignorId(userDetail.getId());
        return ApiResponse.onSuccess(quotationInfoResponses);

    }
}
