package com.example.linkcargo.domain.prediction;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {
    @Query("SELECT p FROM Prediction p WHERE (p.year = :currentYear AND p.month >= :currentMonth) " +
        "OR (p.year = :endYear AND p.month <= :endMonth) " +
        "OR (p.year > :currentYear AND p.year < :endYear)")
    List<Prediction> findPredictionsWithinPeriod(
        @Param("currentYear") int currentYear,
        @Param("currentMonth") int currentMonth,
        @Param("endYear") int endYear,
        @Param("endMonth") int endMonth);

    Prediction findByMonthAndYear(int currentMonth, int currentYear);

    Optional<Prediction> findByYearAndMonth(Integer year, Integer month);
}
