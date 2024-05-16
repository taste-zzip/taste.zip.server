package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

}
