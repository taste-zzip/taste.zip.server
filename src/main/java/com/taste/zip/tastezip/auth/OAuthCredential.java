package com.taste.zip.tastezip.auth;

import com.taste.zip.tastezip.dto.AccountDetailResponse.AccountDetailResponseBuilder;
import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.entity.AccountConfig;
import com.taste.zip.tastezip.entity.AccountOAuth;
import java.util.List;
import lombok.Builder;

/**
 * @see com.google.api.client.auth.oauth2.TokenResponse (를 참고 했음)
 */
public record OAuthCredential(
    Token token,
    User user
) {

    @Builder
    public record Token(
        String accessToken,
        String refreshToken,
        Long expireSeconds,
        String tokenType,
        String scope
    ) {

    }

    @Builder(builderMethodName = "hiddenBuilder")
    public record User(
        String oauthPk,
        String email,
        String profileImage,
        String rawData
    ) {
        public static UserBuilder builder(String oauthPk) {
            return hiddenBuilder()
                .oauthPk(oauthPk);
        }
    }
}
