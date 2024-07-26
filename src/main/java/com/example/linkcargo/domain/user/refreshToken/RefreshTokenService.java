package com.example.linkcargo.domain.user.refreshToken;

import com.example.linkcargo.global.jwt.JwtProvider;
import com.example.linkcargo.global.response.code.resultCode.ErrorStatus;
import com.example.linkcargo.global.response.exception.handler.JwtHandler;
import io.jsonwebtoken.Jwt;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    public void save(Long userId, String token) {
        RefreshToken refreshToken = new RefreshToken(userId, token);
        refreshTokenRepository.save(refreshToken);
    }

    public void validate(Long userId, String refreshToken) {
        RefreshToken tokenEntity = refreshTokenRepository.findByUserIdAndToken(userId, refreshToken)
            .orElseThrow(() -> new JwtHandler(ErrorStatus.REFRESH_TOKEN_NOT_FOUND));

        Date expirationDate = jwtProvider.getExpiration(tokenEntity.getToken());
        if (expirationDate.before(new Date())) {
            throw new JwtHandler(ErrorStatus.REFRESH_TOKEN_EXPIRED);
        }
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
