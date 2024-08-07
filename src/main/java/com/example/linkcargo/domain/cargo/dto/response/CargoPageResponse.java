package com.example.linkcargo.domain.cargo.dto.response;

import com.example.linkcargo.domain.cargo.dto.CargoDTO;
import java.util.List;

public record CargoPageResponse(
    List<CargoDTO> cargos,
    Long totalCount,
    int page,
    int per_page
) {

}
