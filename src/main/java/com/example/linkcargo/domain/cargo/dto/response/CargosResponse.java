package com.example.linkcargo.domain.cargo.dto.response;

import com.example.linkcargo.domain.cargo.dto.CargoDTO;
import java.util.List;

public record CargosResponse(
    List<CargoDTO> cargos
) {

}
