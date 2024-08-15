package com.example.linkcargo.domain.schedule;

import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    boolean existsByCarrierAndETDAndETAAndTransportType(String carrier, LocalDateTime etd, LocalDateTime eta, TransportType transportType);
    Page<Schedule> findByExportPortIdAndImportPortIdAndETDAfterAndLimitCBM(
        Long exportPortId,
        Long importPortId,
        LocalDateTime etdAfter,
        Integer limitCBM,
        Pageable pageable
    );
}
