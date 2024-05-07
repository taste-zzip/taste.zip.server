package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.Cafeteria;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeteriaRepository extends JpaRepository<Cafeteria, Long> {

    List<Cafeteria> findByNameContainingAndTypeContaining(String keyword);
}
