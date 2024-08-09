package com.example.linkcargo.domain.image;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {
    @Query("SELECT i.url FROM Image i WHERE i.name LIKE %:keyword%")
    List<String> findUrlsByNameContaining(@Param("keyword") String keyword);
}
