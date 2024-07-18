package com.example.linkcargo.domain.refreshToken;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    void deleteAllByUserId(Long userId);

    boolean existsByUserId(Long userId);

    boolean existsByUserIdAndToken(Long userId, String refreshToken);
}
