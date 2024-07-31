package com.example.linkcargo.domain.token;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByUserIdAndToken(Long userId, String refreshToken);

    void deleteByUserId(Long userId);
}
