package com.example.linkcargo.domain.user.refreshToken;

import com.example.linkcargo.global.jwt.TokenDTO;

public record RefreshTokenResponse(
    TokenDTO tokens
) {

}
