package com.example.linkcargo.domain.token.dto.request;

public record UserLoginRequest(
    String email,
    String password
) {

}
