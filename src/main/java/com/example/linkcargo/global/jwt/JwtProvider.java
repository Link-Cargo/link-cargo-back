package com.example.linkcargo.global.jwt;

import com.example.linkcargo.global.security.CustomUserDetailsService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {
    private long ACCESS_EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1 day
    private long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; //  1 week
    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private final CustomUserDetailsService customUserDetailsService;

    /**
     * JWT ACCESS 토큰 생성
     */
    public String generateAccessToken(Long userId, String email) {
        log.info("generateToken 메서드 시작");
        return "Bearer " + Jwts.builder()
            .claim("id", userId)
            .claim("email", email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
            .signWith(secretKey)
            .compact();
    }

    /**
     * JWT REFRESH 토큰 생성
     */
    public String generateRefreshToken(Long userId, String email) {
        return Jwts.builder()
            .claim("id", userId)
            .claim("email", email)
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
            .signWith(secretKey)
            .compact();
    }

}