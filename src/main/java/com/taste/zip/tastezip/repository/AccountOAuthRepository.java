package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.AccountOAuth;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountOAuthRepository extends JpaRepository<AccountOAuth, Long> {

    boolean existsByTypeAndOauthPk(OAuthType type, String oauthPk);
}