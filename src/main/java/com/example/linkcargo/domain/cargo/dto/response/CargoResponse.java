package com.example.linkcargo.domain.cargo.dto.response;

import com.example.linkcargo.domain.cargo.Cargo;
import jakarta.validation.constraints.NotNull;

public record CargoResponse(
    @NotNull
    Cargo cargo
) {

}
