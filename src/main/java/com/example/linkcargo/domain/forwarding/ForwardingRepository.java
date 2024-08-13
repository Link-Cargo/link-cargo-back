package com.example.linkcargo.domain.forwarding;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ForwardingRepository extends JpaRepository<Forwarding, Long> {
    boolean existsByFirmName(String firmName);
}
