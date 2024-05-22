package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.entity.AccountOAuth;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOAuthRepository extends JpaRepository<AccountOAuth, Long> {

    boolean existsByTypeAndOauthPk(OAuthType type, String oauthPk);

    Optional<AccountOAuth> findByTypeAndAccount_Id(OAuthType type, Long accountId);

    Optional<AccountOAuth> findByTypeAndOauthPk(OAuthType type, String oauthPk);

    List<AccountOAuth> findAllByAccount_Id(Long accountId);
}
