package com.example.linkcargo.global.jwt;

import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.JwtHandler;
import com.example.linkcargo.global.security.CustomUserDetail;
import com.example.linkcargo.global.security.CustomUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class JwtProvider {

    private final CustomUserDetailsService customUserDetailsService;
    private long ACCESS_EXPIRATION_TIME = 1000 * 60 * 2; // 2 minutes
    private long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 24 * 7; //  1 week
    private Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /**
     * JWT ACCESS 토큰 생성
     */
    public String generateAccessToken(Long userId, String email) {
        log.info("generateToken 메서드 시작");
        return Jwts.builder()
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

    /**
     * JWT 에서 Claims 추출
     */
    public Claims getClaimsBody(String token) {
        try {
            return Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (SignatureException e) {
            throw new JwtHandler(ErrorStatus.INVALID_ACCESS_TOKEN);
        } catch (MalformedJwtException e) {
            throw new JwtHandler(ErrorStatus.INVALID_ACCESS_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new JwtHandler(ErrorStatus.EXPIRED_MEMBER_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new JwtHandler(ErrorStatus.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new JwtHandler(ErrorStatus.ILLEGAL_ARGUMENT_TOKEN);
        } catch (PrematureJwtException e) {
            throw new JwtHandler(ErrorStatus.INVALID_ACCESS_TOKEN);
        }
    }

    /**
     * 현재 로그인한 사용자 ID 추출
     */
    public Long getId(String token) {
        Claims claimsBody = getClaimsBody(token);

        Long userId =
            (claimsBody.get("id") instanceof Integer) ? Long.valueOf((Integer) claimsBody.get("id"))
                : (Long) claimsBody.get("id");

        return userId;
    }

    /**
     * 현재 로그인한 사용자 Email 추출
     */
    public String getEmail(String token) {
        Claims claimsBody = getClaimsBody(token);

        return claimsBody.get("email").toString();
    }

    /**
     * JWT 만료 기간 추출
     */
    public Date getExpiration(String token) {
        Claims claimsBody = getClaimsBody(token);
        return claimsBody.getExpiration();
    }

    /**
     * (인가) Authorization 객체 생성
     */
    public Authentication getAuthentication(String token) {
        CustomUserDetail customUserDetail = customUserDetailsService.loadUserByUsername(
            getEmail(token));

        return new UsernamePasswordAuthenticationToken(customUserDetail,
            customUserDetail.getPassword(), customUserDetail.getAuthorities());
    }
}