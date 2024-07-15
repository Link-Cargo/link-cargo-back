package com.example.linkcargo.domain.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequestDTO {

    @NotBlank
    @Email
    public String email;

    @NotBlank
    public String password;

    @Override
    public String toString() {
        return "LoginRequestDTO{" +
               "email='" + email + '\'' +
               ", password='" + password + '\'' +
               '}';
    }
}
