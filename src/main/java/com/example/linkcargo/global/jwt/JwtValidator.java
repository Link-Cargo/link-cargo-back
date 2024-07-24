package com.example.linkcargo.global.jwt;

import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

    public String validateFormAndRemoveBearer(String rawToken) {
        if (rawToken == null || !rawToken.startsWith("Bearer")) {
            throw new JwtException("JWT 토큰이 없거나 유효하지 않은 형식입니다.");
        }

        String token = rawToken.substring(7);
        if (token.isEmpty()) {
            throw new JwtException("JWT 토큰이 존재하지 않습니다.");
        }
        return token;
    }
}
