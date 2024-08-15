package com.example.linkcargo.domain.port.dto.response;

import com.example.linkcargo.domain.port.Port;
import lombok.Builder;

@Builder
public record PortReadResponse(
        Long id,
        String name,
        String type
) {
    public static PortReadResponse fromEntity(Port port){
        return  PortReadResponse.builder()
                .id(port.getId())
                .name(port.getName())
                .type(String.valueOf(port.getType()))
                .build();
    }
}
