package com.example.linkcargo.global.jwt;

import com.example.linkcargo.domain.user.User;
import com.example.linkcargo.domain.user.dto.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.PrematureJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    @Value("${jwt.secret.key")
    private String SECRET_KEY;
    private long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 1 day


    /**
     * JWT 토큰 생성
     */
    public String generateToken(User user) {
        return Jwts.builder()
            .claim("id", user.getId())
            .claim("email", user.getEmail())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
            .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
            .compact();
    }

    /**
     * JWT 에서 Claims 추출
     */
    public Claims getClaimsBody(String token) {
        try {
            return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (SignatureException e) {
            throw new JwtException("JWT 서명이 올바르지 않습니다.");
        } catch (MalformedJwtException e) {
            throw new JwtException("JWT 토큰 형식이 잘못되었습니다.");
        } catch (ExpiredJwtException e) {
            throw new JwtException("JWT 유효기간이 만료되었습니다.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 JWT 형식입니다.");
        } catch (IllegalArgumentException e) {
            throw new JwtException("JWT 문자열이 올바르지 않습니다.");
        } catch (PrematureJwtException e) {
            throw new JwtException("JWT 가 아직 활성화 되지 않았습니다.");
        }
    }

    /**
     * 현재 로그인한 사용자 정보 추출
     */
    public UserInfo getUserInfo(String token) {
        Claims claimsBody = getClaimsBody(token);

        UserInfo currentUser = new UserInfo(
            (claimsBody.get("id") instanceof Integer) ? Long.valueOf((Integer) claimsBody.get("id"))
                : (Long) claimsBody.get("id"), claimsBody.get("email").toString());

        return currentUser;
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
}
