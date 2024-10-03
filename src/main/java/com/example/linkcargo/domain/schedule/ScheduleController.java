package com.example.linkcargo.domain.schedule;

import com.example.linkcargo.domain.schedule.dto.request.ScheduleCreateUpdateRequest;
import com.example.linkcargo.domain.schedule.dto.response.ScheduleInfoResponse;
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
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "3. Schedule", description = "선박 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "선박 스케줄 생성 ", description = "선박 스케줄을 생성 합니다. ScheduleCreateUpdateRequest 사용")
    @PostMapping("")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE401", description = "이미 존재하는 선박스케줄 입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE402", description = "선박정보 생성에 실패하였습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<Long> createSchedule(@AuthenticationPrincipal CustomUserDetail userDetail,
        @RequestBody ScheduleCreateUpdateRequest request) {
        Long resultId = scheduleService.createSchedule(request, userDetail.getId());
        return ApiResponse.onSuccess(resultId);
    }

    @Operation(summary = "선박 스케줄 단일 조회 ", description = "선박 스케줄 아이디에 따라 선박 스케줄을 조회 합니다. ScheduleInfoResponse 사용")
    @GetMapping("/{scheduleId}")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403", description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<ScheduleInfoResponse> findSchedule(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "선박 스케줄 아이디") @PathVariable("scheduleId") Long scheduleId) {
        ScheduleInfoResponse scheduleInfoResponse = scheduleService.findSchedule(scheduleId);
        return ApiResponse.onSuccess(scheduleInfoResponse);
    }

    @Operation(summary = "선박 스케줄 리스트 조회 ", description = "모든 선박 스케줄을 조회 합니다. ScheduleListResponse 사용")
    @GetMapping("/list")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<ScheduleListResponse> findSchedules(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
    ) {
        ScheduleListResponse scheduleListResponse = scheduleService.findSchedules(page, size);
        return ApiResponse.onSuccess(scheduleListResponse);
    }

    @Operation(summary = "선박 스케줄 변경 ", description = "선박 스케줄을 변경합니다. ScheduleCreateUpdateRequest 사용 ")
    @PutMapping("/{scheduleId}")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403", description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE404", description = "선박 스케줄 변경에 실패했습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<SuccessStatus> modifySchedule(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "선박 스케줄 아이디") @PathVariable Long scheduleId,
        @RequestBody ScheduleCreateUpdateRequest request
    ) {
        scheduleService.modifySchedule(scheduleId, request, userDetail.getId());
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @Operation(summary = "선박 스케줄 삭제", description = "선박 스케줄을 삭제합니다.")
    @DeleteMapping("/{scheduleId}")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403", description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE405", description = "선박 스케줄 삭제에 실패했습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<SuccessStatus> removeSchedule(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "선박 스케줄 아이디") @PathVariable Long scheduleId
    ) {
        scheduleService.removeSchedule(scheduleId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

    @Operation(summary = "선박 스케줄 검색", description = "수출항, 수입항 번호가 일치하면서, ETD 기준으로 필터링 및 정렬된 선박 스케줄을 검색합니다.")
    @GetMapping("/search")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<ScheduleListResponse> searchSchedules(
            @AuthenticationPrincipal CustomUserDetail userDetail,
            @Parameter(description = "수출항 ID") @RequestParam Long exportPortId,
            @Parameter(description = "수입항 ID") @RequestParam Long importPortId,
            @Parameter(description = "화주가 입력한 화물의 CBM") @RequestParam Double inputCBM,
            @Parameter(description = "스케줄 날짜") @RequestParam LocalDate searchDate,
            @Parameter(description = "페이지 번호 (0부터 시작)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
    ) {
        ScheduleListResponse schedules = scheduleService.searchSchedules(exportPortId, importPortId, inputCBM, searchDate, page, size);
        return ApiResponse.onSuccess(schedules);
    }

    @Operation(summary = "포워더의 스케줄 검색", description = "포워더가 생성한 스케줄을 조회합니다.")
    @GetMapping("/forwarder/{forwarderId}")
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "OK, 성공"),
    })
    public ApiResponse<List<ScheduleInfoResponse>> findSchedulesByForwarderId(
        @AuthenticationPrincipal CustomUserDetail userDetail,
        @Parameter(description = "포워더 아이디") @PathVariable Long forwarderId
    ) {
        List<ScheduleInfoResponse> schedules = scheduleService.findSchedulesByForwarderId(forwarderId);
        return ApiResponse.onSuccess(schedules);
    }

}
