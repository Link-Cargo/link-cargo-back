package com.example.linkcargo.domain.schedule;

import com.example.linkcargo.domain.schedule.dto.request.ScheduleCreateUpdateRequest;
import com.example.linkcargo.domain.schedule.dto.response.ScheduleInfoResponse;
import com.example.linkcargo.domain.schedule.dto.response.ScheduleListResponse;
import com.example.linkcargo.global.resolver.Login;
import com.example.linkcargo.global.resolver.LoginInfo;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.response.code.resultCode.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
@Tag(name = "3. Schedule", description = "선박 정보 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    @Operation(summary = "선박 스케줄 생성 ", description = "선박 스케줄을 생성 합니다. ScheduleCreateUpdateRequest 사용")
    @PostMapping("")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE401",description = "이미 존재하는 선박스케줄 입니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE402",description = "선박정보 생성에 실패하였습니다", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<Long> createSchedule(@Login LoginInfo loginInfo, @RequestBody ScheduleCreateUpdateRequest request) {
        Long resultId = scheduleService.createSchedule(request);
        return ApiResponse.onSuccess(resultId);
    }

    @Operation(summary = "선박 스케줄 단일 조회 ", description = "선박 스케줄 아이디에 따라 선박 스케줄을 조회 합니다. ScheduleInfoResponse 사용")
    @GetMapping("/{scheduleId}")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403",description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<ScheduleInfoResponse> findSchedule(
            @Login LoginInfo loginInfo,
            @Parameter(description = "선박 스케줄 아이디") @PathVariable("scheduleId") Long scheduleId) {
        ScheduleInfoResponse scheduleInfoResponse = scheduleService.findSchedule(scheduleId);
        return ApiResponse.onSuccess(scheduleInfoResponse);
    }

    @Operation(summary = "선박 스케줄 리스트 조회 ", description = "모든 선박 스케줄을 조회 합니다. ScheduleListResponse 사용")
    @GetMapping("")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
    })
    public ApiResponse<ScheduleListResponse> findSchedules(
            @Login LoginInfo loginInfo,
            @Parameter(description = "페이지 번호") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기") @RequestParam(defaultValue = "10") int size
    ) {
        ScheduleListResponse scheduleListResponse = scheduleService.findSchedules(page, size);
        return ApiResponse.onSuccess(scheduleListResponse);
    }

    @Operation(summary = "선박 스케줄 변경 ", description = "선박 스케줄을 변경합니다. ScheduleCreateUpdateRequest 사용 ")
    @PutMapping("")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200",description = "OK, 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE403",description = "선박 스케줄이 존재 하지 않습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "SCHEDULE404",description = "선박 스케줄 변경에 실패했습니다.", content = @Content(schema = @Schema(implementation = ApiResponse.class))),
    })
    public ApiResponse<SuccessStatus> modifySchedule(
            @Login LoginInfo loginInfo,
            @Parameter(description = "선박 스케줄 아이디") @RequestParam Long scheduleId,
            @RequestBody ScheduleCreateUpdateRequest request
    ) {
        scheduleService.modifySchedule(scheduleId, request);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }

}
