package com.example.linkcargo.domain.cargo.dto.response;

import com.example.linkcargo.domain.cargo.Cargo;
import java.util.List;

public record CargoPageResponse(
    List<Cargo> cargos,
    Long totalCount,
    int page,
    int per_page
) {

}
