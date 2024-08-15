package com.example.linkcargo.domain.user.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UserPasswordUpdateRequest(

    @NotBlank
    String existPassword,
    @NotBlank
    String newPassword
) {

}
