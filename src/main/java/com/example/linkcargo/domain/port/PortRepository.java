package com.example.linkcargo.domain.port;

import org.springframework.data.jpa.repository.JpaRepository;


public interface PortRepository extends JpaRepository <Port, Long> {
    boolean existsByName(String name);
}
