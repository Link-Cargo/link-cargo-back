package com.example.linkcargo.domain.port;

import com.example.linkcargo.domain.port.dto.request.PortCreateUpdateRequest;
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

@Tag(name = "5. Port", description = "항구 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ports")
public class PortController {

    private final PortService portService;
    @Operation(summary = "항구 생성 ", description = "항구를 생성 합니다. PortCreateUpdateRequest 사용")
    @PostMapping("")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PORT403",description = "이미 존재하는 항구 입니다..", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PORT404",description = "항구 생성에 실패하였습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<Long> createPort(@AuthenticationPrincipal CustomUserDetail userDetail, @RequestBody PortCreateUpdateRequest request) {
        Long resultId = portService.createPort(request);
        return ApiResponse.onSuccess(resultId);
    }
}
