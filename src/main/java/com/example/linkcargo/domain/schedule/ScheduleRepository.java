package com.example.linkcargo.domain.schedule;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    boolean existsByCarrierAndETDAndETAAndTransportType(String carrier, LocalDateTime etd, LocalDateTime eta, TransportType transportType);
    Page<Schedule> findByETDAfter(LocalDateTime ETD, Pageable pageable);

}
