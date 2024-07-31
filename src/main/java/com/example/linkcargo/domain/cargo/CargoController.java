package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import com.example.linkcargo.domain.cargo.dto.response.CargoPageResponse;
import com.example.linkcargo.domain.cargo.dto.response.CargoResponse;
import com.example.linkcargo.global.resolver.Login;
import com.example.linkcargo.global.resolver.LoginInfo;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.response.code.resultCode.SuccessStatus;
import com.example.linkcargo.global.security.CustomUserDetail;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

@Tag(name = "2. Cargo", description = "화물 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users/cargos")
public class CargoController {

    private final CargoService cargoService;

    @Operation(summary = "화물 추가", description = "화물 정보를 입력합니다. CargoRequest 사용")
    @PostMapping
    public ApiResponse<String> createCargo(@Login LoginInfo loginInfo, @Valid @RequestBody CargoRequest cargoRequest) {
        Cargo savedCargo = cargoService.createCargo(loginInfo.id(), cargoRequest);
        return ApiResponse.onSuccess(savedCargo.getId());
    }

    @Operation(summary = "화물 조회", description = "특정 화물을 조회합니다.")
    @GetMapping("/{cargoId}")
    public ApiResponse<CargoResponse> getCargo(@PathVariable("cargoId") String cargoId) {
        Cargo cargo = cargoService.getCargo(cargoId);
        return ApiResponse.onSuccess(new CargoResponse(cargo));
    }

    @Operation(summary = "나의 화물 목록 조회 - 페이징", description = "내가 추가한 화물의 목록을 조회합니다.")
    @GetMapping("/all")
    public ApiResponse<CargoPageResponse> getMyCargos(
        @Parameter(description = "페이지 번호") @RequestParam(value = "page", defaultValue = "0") int page,
        @Parameter(description = "페이지 크기") @RequestParam(value = "size", defaultValue = "10") int size,
        @Parameter(description = "정렬 기준") @RequestParam(value = "sort", defaultValue = "updatedAt_desc") String sort,
        @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        PageRequest pageRequest = PageRequest.of(page, size, getSortObject(sort));
        CargoPageResponse cargoPageResponse = cargoService.getMyCargos(userDetail.getId(), pageRequest);
        return ApiResponse.onSuccess(cargoPageResponse);
    }

    @Operation(summary = "나의 화물 수정", description = "나의 화물을 수정합니다. CargoRequest 사용")
    @PutMapping("/{cargoId}")
    public ApiResponse<String> updateMyCargo(
        @PathVariable("cargoId") String cargoId,
        @RequestBody CargoRequest cargoRequest,
        @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        Cargo udpatedCargo = cargoService.updateMyCargo(userDetail.getId(), cargoId, cargoRequest);
        return ApiResponse.onSuccess(udpatedCargo.getId());
    }

    @Operation(summary = "나의 화물 삭제", description = "나의 화물을 삭제합니다.")
    @DeleteMapping("{cargoId}")
    public ApiResponse<SuccessStatus> deleteMyCargo(
        @PathVariable("cargoId") String cargoId,
        @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        cargoService.deleteMyCargo(userDetail.getId(), cargoId);
        return ApiResponse.onSuccess(SuccessStatus._OK);
    }


    private Sort getSortObject(String sort) {
        switch (sort) {
            case "updatedAt_asc":
                return Sort.by(Sort.Direction.ASC, "updatedAt");
            default:
                return Sort.by(Sort.Direction.DESC, "updatedAt");
        }
    }
}
