package com.example.linkcargo.global.jwt;

public record TokenDTO(
    String accessToken,
    String refreshToken
) {

}
