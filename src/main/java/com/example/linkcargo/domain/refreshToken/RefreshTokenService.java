package com.example.linkcargo.domain.refreshToken;

import com.example.linkcargo.global.exception.BusinessException;
import java.io.PushbackReader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    public void saveRefreshToken(Long userId, String refreshToken) {
        if(refreshTokenRepository.existsByUserId(userId)){
            refreshTokenRepository.deleteAllByUserId(userId);
        }

        refreshTokenRepository.save(new RefreshToken(userId, refreshToken));
    }

    public void validate(Long userId, String refreshToken) {
        if(!refreshTokenRepository.existsByUserIdAndToken(userId, refreshToken)){
            throw new BusinessException(HttpStatus.BAD_REQUEST, "리프레시 토큰이 유효하지 않습니다.");
        }
    }
}
