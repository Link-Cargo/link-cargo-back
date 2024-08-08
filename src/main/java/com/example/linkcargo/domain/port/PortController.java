package com.example.linkcargo.domain.port;

import com.example.linkcargo.domain.port.dto.request.PortCreateUpdateRequest;
import com.example.linkcargo.domain.port.dto.response.PortReadResponse;
import com.example.linkcargo.domain.schedule.dto.response.ScheduleListResponse;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.response.code.resultCode.SuccessStatus;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Operation(summary = "항구 리스트 조회 ", description = "모든 항구를 조회 합니다. PortReadResponse 사용")
    @GetMapping("")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<List<PortReadResponse>> findPorts(
            @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        List<PortReadResponse> portList = portService.findPorts();
        return ApiResponse.onSuccess(portList);
    }

    @Operation(summary = "항구 업데이트", description = "항구 정보를 업데이트 합니다. PortCreateUpdateRequest 사용")
    @PutMapping("/{portId}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PORT403",description = "이미 존재하는 항구 입니다..", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PORT405", description = "업데이트할 항구를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PORT406", description = "항구 업데이트에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<SuccessStatus> modifyPort(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @PathVariable Long portId,
            @RequestBody PortCreateUpdateRequest request) {
        portService.modifyPort(portId, request);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @Operation(summary = "항구 삭제", description = "항구를 삭제합니다.")
    @DeleteMapping("/{portId}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PORT405", description = "업데이트할 항구를 찾을 수 없습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "PORT407", description = "항구 삭제에 실패하였습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<SuccessStatus> removePort(@AuthenticationPrincipal CustomUserDetail userDetail, @PathVariable Long portId) {
        portService.removePort(portId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }
}
