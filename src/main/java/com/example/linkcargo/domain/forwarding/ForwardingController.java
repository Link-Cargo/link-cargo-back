package com.example.linkcargo.domain.forwarding;

import com.example.linkcargo.domain.forwarding.dto.request.ForwardingCreateUpdateRequest;
import com.example.linkcargo.global.response.ApiResponse;
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

@Tag(name = "7. Forwarding", description = "포워딩 업체 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/forwardings")
public class ForwardingController {

    private final ForwardingService forwardingService;

    @Operation(summary = "포워딩 업체 생성", description = "포워딩 업체를 생성합니다.. ForwardingCreateUpdateRequest 사용")
    @PostMapping("")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FORWARDING401", description = "이미 존재하는 포워딩 업체 입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "FORWARDING402", description = "포워딩 업체 생성에 실패했습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<Long> createForwarding(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @RequestBody ForwardingCreateUpdateRequest request)
    {
        Long resultId = forwardingService.createForwarding(request);
        return ApiResponse.onSuccess(resultId);
    }
}
