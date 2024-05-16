package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.AdminConfig;
import com.taste.zip.tastezip.entity.enumeration.AdminConfigType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminConfigRepository extends JpaRepository<AdminConfig, Long> {

    Optional<AdminConfig> findByType(AdminConfigType type);
}
