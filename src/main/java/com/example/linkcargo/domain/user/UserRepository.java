package com.example.linkcargo.domain.user;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByEmail(String email);

    boolean existsByBusinessNumber(String email);

    Optional<User> findByEmail(String email);

    List<User> findAllByRole(Role role);
}
