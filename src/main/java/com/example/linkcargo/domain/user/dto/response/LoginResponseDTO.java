package com.example.linkcargo.domain.user.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDTO {

    public String token;

    public LoginResponseDTO(String token) {
        this.token = token;
    }
}
