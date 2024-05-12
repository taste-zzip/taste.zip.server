package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.Cafeteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CafeteriaRepository extends JpaRepository<Cafeteria, Long> {

    // using JPQL
    @Query("SELECT c FROM Cafeteria c WHERE c.name LIKE %:keyword% OR c.type LIKE %:keyword%")
    Page<Cafeteria> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

}
