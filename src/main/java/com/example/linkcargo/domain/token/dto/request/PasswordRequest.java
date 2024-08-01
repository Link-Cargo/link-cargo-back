package com.example.linkcargo.domain.token.dto.request;

import jakarta.validation.constraints.NotBlank;

public record PasswordRequest(
    @NotBlank
    String password
) {

}
