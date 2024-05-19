package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.entity.AccountOAuth;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOAuthRepository extends JpaRepository<AccountOAuth, Long> {

    boolean existsByTypeAndOauthPk(OAuthType type, String oauthPk);

    List<AccountOAuth> findAllByAccount_Id(Long accountId);
}
