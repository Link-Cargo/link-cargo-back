package com.example.linkcargo.domain.cargo;

import com.example.linkcargo.domain.cargo.dto.request.CargoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public Cargo createCargo(@Valid @RequestBody CargoRequest cargoRequest) {
        Cargo savedCargo = cargoService.createCargo(cargoRequest);
        return savedCargo;
    }
}
