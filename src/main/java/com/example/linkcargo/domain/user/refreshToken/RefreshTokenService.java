package com.example.linkcargo.domain.user.refreshToken;

import com.example.linkcargo.global.jwt.JwtProvider;
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
            .orElseThrow(() -> new RuntimeException("리프레시 토큰이 존재하지 않습니다."));

        Date expirationDate = jwtProvider.getExpiration(tokenEntity.getToken());
        if (expirationDate.before(new Date())) {
            throw new RuntimeException("리프레시 토큰이 만료되었습니다.");
        }
    }

    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
