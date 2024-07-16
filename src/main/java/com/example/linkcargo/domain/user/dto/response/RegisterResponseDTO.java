package com.example.linkcargo.domain.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponseDTO {

    private Long userId;
    private String email;

    public RegisterResponseDTO(Long id, String email) {
        this.userId = id;
        this.email = email;
    }
}
