package com.example.linkcargo.domain.user.dto.response;

import com.example.linkcargo.global.jwt.TokenDTO;

public record UserLoginResponse(
    TokenDTO tokens
) {

}
