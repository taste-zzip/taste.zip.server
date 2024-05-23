package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.AccountCafeteriaMapping;
import com.taste.zip.tastezip.entity.enumeration.AccountCafeteriaMappingType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountCafeteriaMappingRepository extends JpaRepository<AccountCafeteriaMapping, Long> {

    boolean existsByTypeAndAccount_IdAndCafeteriaId(AccountCafeteriaMappingType type, Long accountId, Long cafeteriaId);
    Optional<AccountCafeteriaMapping> findByTypeAndAccount_IdAndCafeteriaId(AccountCafeteriaMappingType type, Long accountId, Long cafeteriaId);

    List<AccountCafeteriaMapping> findAllByTypeAndAccount_Id(AccountCafeteriaMappingType type, Long accountId);
}
