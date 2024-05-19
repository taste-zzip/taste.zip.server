package com.taste.zip.tastezip.auth;

import com.taste.zip.tastezip.auth.TokenProvider.Type;
import lombok.Builder;

/**
 * Authentication payload object
 * https://datatracker.ietf.org/doc/html/rfc7519#section-4.3
 * @param userId
 * @see com.taste.zip.tastezip.auth.TokenJwtProvider
 */
@Builder(builderMethodName = "hiddenBuilder")
public record TokenDetail(
    Long userId
) {

    public static TokenDetailBuilder builder(Long userId) {
        return hiddenBuilder()
            .userId(userId);
    }
}
