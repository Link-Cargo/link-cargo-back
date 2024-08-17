package com.example.linkcargo.domain.news;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NewsRepository extends JpaRepository<News, Long> {
    @Query("SELECT n FROM News n WHERE n.title LIKE %:category% AND FUNCTION('DATE', n.createdAt) = :createdAt")
    List<News> findByCategoryAndCreatedDate(@Param("category") String category, @Param("createdAt") LocalDate createdAt);

}
