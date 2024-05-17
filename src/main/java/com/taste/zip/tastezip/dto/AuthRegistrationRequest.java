package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.entity.enumeration.AccountConfigType;
import com.taste.zip.tastezip.entity.enumeration.AccountType;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record AuthRegistrationRequest(
    @NotNull(message = "{account.register.notnull}")
    String nickname,
    String bio,
    String profileImage,
    @NotNull(message = "{account.register.notnull}")
    AccountType type,
    @Valid Config config,
    @Valid OAuth oauth
) {
    public record OAuth(
        @NotNull(message = "{account.register.notnull}")
        OAuthType type,
        @NotNull(message = "{account.register.notnull}")
        String oauthPk,
        String accessToken,
        String refreshToken,
        String email,
        String profileImage,
        String rawData
    ) {

    }

    /**
     * @see com.taste.zip.tastezip.entity.enumeration.AccountConfigType
     * 필드 이름 잘 맞출 것, 오타 조심!
     */
    public record Config(
        @NotNull(message = "{account.register.notnull}")
        AccountConfigType.Agreement TERM_OF_USE_AGREEMENT,
        @NotNull(message = "{account.register.notnull}")
        AccountConfigType.Agreement TERM_OF_GPS_AGREEMENT,
        @NotNull(message = "{account.register.notnull}")
        AccountConfigType.Agreement MARKETING_MESSAGE_AGREEMENT
    ) {

    }
}
