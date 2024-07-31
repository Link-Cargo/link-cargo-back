package com.example.linkcargo.domain.cargo.dto.response;

import com.example.linkcargo.domain.cargo.Cargo;
import java.util.List;

public record CargosResponse(
    List<Cargo> cargos
) {

}
