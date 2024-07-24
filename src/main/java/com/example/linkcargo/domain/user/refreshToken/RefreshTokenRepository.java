package com.example.linkcargo.domain.user.refreshToken;

import java.util.Optional;
import javax.swing.text.html.Option;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    boolean existsByUserIdAndToken(Long userId, String refreshToken);

    Optional<RefreshToken> findByUserIdAndToken(Long userId, String refreshToken);

    void deleteByUserId(Long userId);
}
