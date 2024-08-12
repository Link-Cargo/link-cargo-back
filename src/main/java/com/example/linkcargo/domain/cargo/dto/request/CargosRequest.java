package com.example.linkcargo.domain.cargo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public record CargosRequest(
    @NotNull(message = "export port id is mandatory")
    Long exportPortId, // 출발항 ID

    @NotNull(message = "export port id is mandatory")
    Long importPortId, // 도착항 ID

    LocalDateTime wishExportDate, // 희망 출하 날짜

    @NotBlank(message = "Incoterms is required")
    @Size(max = 20, message = "Incoterms must be less than 20 characters")
    String incoterms, // 인코텀즈
    List<CargoRequest> cargos // 화물 정보 목록
) {

}
