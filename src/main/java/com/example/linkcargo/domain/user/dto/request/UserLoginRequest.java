package com.example.linkcargo.domain.user.dto.request;

public record UserLoginRequest(
    String email,
    String password
) {

}
