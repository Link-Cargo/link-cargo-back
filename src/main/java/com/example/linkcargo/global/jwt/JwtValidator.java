package com.example.linkcargo.global.jwt;

import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.JwtHandler;
import io.jsonwebtoken.JwtException;
import org.springframework.stereotype.Component;

@Component
public class JwtValidator {

    public String validateFormAndRemoveBearer(String rawToken) {
        if (rawToken == null || !rawToken.startsWith("Bearer")) {
            throw new JwtHandler(ErrorStatus.INVALID_ACCESS_TOKEN);
        }

        String token = rawToken.substring(7);
        if (token.isEmpty()) {
            throw new JwtHandler(ErrorStatus.INVALID_ACCESS_TOKEN);
        }
        return token;
    }
}