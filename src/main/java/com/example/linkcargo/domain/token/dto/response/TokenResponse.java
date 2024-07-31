package com.example.linkcargo.domain.token.dto.response;

public record TokenResponse(
    String accessToken,
    String refreshToken
) {

}
