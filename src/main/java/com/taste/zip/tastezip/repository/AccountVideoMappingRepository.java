package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.AccountVideoMapping;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountVideoMappingRepository extends JpaRepository<AccountVideoMapping, Long> {

    List<AccountVideoMapping> findAllByAccount_IdAndVideoId(Long accountId, Long videoId);
}
