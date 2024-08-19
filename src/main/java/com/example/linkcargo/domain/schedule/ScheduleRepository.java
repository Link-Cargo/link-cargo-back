package com.example.linkcargo.domain.schedule;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    boolean existsByCarrierAndETDAndETAAndTransportType(String carrier, LocalDateTime etd, LocalDateTime eta, TransportType transportType);
    Page<Schedule> findByExportPortIdAndImportPortIdAndETDAfterAndLimitCBM(
        Long exportPortId,
        Long importPortId,
        LocalDateTime etdAfter,
        Integer limitCBM,
        Pageable pageable
    );

    @Query("SELECT s FROM Schedule s WHERE " +
        "(FUNCTION('YEAR', s.ETD) = :year AND FUNCTION('MONTH', s.ETD) = :month) OR " +
        "(FUNCTION('YEAR', s.ETA) = :year AND FUNCTION('MONTH', s.ETA) = :month)")
    List<Schedule> findSchedulesByYearMonth(@Param("year") int year, @Param("month") int month);
}
