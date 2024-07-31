package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import com.example.linkcargo.domain.cargo.dto.response.CargoResponse;
import com.example.linkcargo.global.resolver.Login;
import com.example.linkcargo.global.resolver.LoginInfo;
import com.example.linkcargo.global.response.ApiResponse;
import com.example.linkcargo.global.security.CustomUserDetail;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/users/cargos")
public class CargoController {

    private final CargoService cargoService;

    @PostMapping
    public ApiResponse<String> createCargo(@Login LoginInfo loginInfo, @Valid @RequestBody CargoRequest cargoRequest) {
        Cargo savedCargo = cargoService.createCargo(loginInfo.id(), cargoRequest);
        return ApiResponse.onSuccess(savedCargo.getId());
    }

    @GetMapping("/{cargoId}")
    public ApiResponse<CargoResponse> getCargo(@PathVariable("cargoId") String cargoId) {
        Cargo cargo = cargoService.getCargo(cargoId);
        return ApiResponse.onSuccess(new CargoResponse(cargo));
    }

    @GetMapping("/all")
    public ApiResponse<List<Cargo>> getMyCargos(
        @Login LoginInfo loginInfo,
        @AuthenticationPrincipal CustomUserDetail userDetail
    ) {
        System.out.println("loginInfo = " + loginInfo);
        System.out.println("userDetail = " + userDetail);

        List<Cargo> cargos = cargoService.getMyCargos(loginInfo.id());
        return ApiResponse.onSuccess(cargos);
    }
}
