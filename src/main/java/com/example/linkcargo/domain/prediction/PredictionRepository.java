package com.example.linkcargo.domain.prediction;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    @Query("SELECT p FROM Prediction p " +
        "WHERE (p.year > :startYear OR (p.year = :startYear AND p.month >= :startMonth)) " +
        "AND (p.year < :endYear OR (p.year = :endYear AND p.month <= :endMonth)) " +
        "ORDER BY p.year ASC, p.month ASC")
    List<Prediction> findPredictionsWithinPeriod(
        @Param("startYear") int startYear,
        @Param("startMonth") int startMonth,
        @Param("endYear") int endYear,
        @Param("endMonth") int endMonth
    );

    Prediction findByMonthAndYear(int currentMonth, int currentYear);

    Optional<Prediction> findByYearAndMonth(Integer year, Integer month);
}
