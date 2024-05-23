package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.AccountVideoMapping;
import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountVideoMappingRepository extends JpaRepository<AccountVideoMapping, Long> {

    List<AccountVideoMapping> findAllByAccount_IdAndVideoId(Long accountId, Long videoId);

    long countAllByVideoIdAndType(Long videoId, AccountVideoMappingType type);

    boolean existsByTypeAndAccountIdAndVideoId(AccountVideoMappingType type, Long accountId, Long videoId);

    Optional<AccountVideoMapping> findByTypeAndAccountIdAndVideoId(AccountVideoMappingType type, Long accountId, Long videoId);
}
