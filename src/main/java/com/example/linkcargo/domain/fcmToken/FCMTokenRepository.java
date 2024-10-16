package com.example.linkcargo.domain.fcmToken;

import com.example.linkcargo.domain.user.Role;
import com.example.linkcargo.domain.user.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {

    Optional<FCMToken> findByUser(User user);

    Optional<FCMToken> findByUserId(Long userId);

    List<FCMToken> findAllByUserRole(Role role);

    List<FCMToken> findAllByUserIn(List<User> users);
}
