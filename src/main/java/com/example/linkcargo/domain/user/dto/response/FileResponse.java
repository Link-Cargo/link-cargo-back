package com.example.linkcargo.domain.user.dto.response;

public record FileResponse(
    FileDTO file
) {

    public record FileDTO(
        String name,
        String url
    ) {
    }
}
