package com.example.linkcargo.domain.port.dto.request;

import com.example.linkcargo.domain.port.Port;
import com.example.linkcargo.domain.schedule.PortType;
import jakarta.validation.constraints.NotNull;

public record PortCreateUpdateRequest(
        @NotNull(message = "name is mandatory")
        String name,

        @NotNull(message = "type is mandatory")
        String type
        ) {

    public Port toEntity() {
        return Port.builder()
                .name(this.name)
                .type(PortType.valueOf(this.type)).
                build();
    }
}
