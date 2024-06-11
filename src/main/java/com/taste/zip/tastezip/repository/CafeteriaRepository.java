package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.Cafeteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CafeteriaRepository extends JpaRepository<Cafeteria, Long> {

    // using JPQL
    @Query("SELECT c FROM Cafeteria c, Video v WHERE v.cafeteria.id = c.id AND (c.name LIKE %:keyword% OR c.type LIKE %:keyword) GROUP BY c.id HAVING count(v.id) > 0")
    Page<Cafeteria> findByKeyword(@Param("keyword") String keyword, Pageable pageable);

    List<Cafeteria> findTop3ByType(String type);
    List<Cafeteria> findTop2ByType(String type);

}
