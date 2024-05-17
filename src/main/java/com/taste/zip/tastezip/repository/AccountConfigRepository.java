package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.AccountConfig;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountConfigRepository extends JpaRepository<AccountConfig, Long> {

    List<AccountConfig> findAllByAccount_Id(Long accountId);
}
