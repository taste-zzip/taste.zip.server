package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.auth.OAuthProvider;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import java.net.URI;
import java.util.List;
import lombok.Builder;

@Builder
public record OAuthLoginUriResponse(
    List<OAuthLoginUri> oauth
) {

    @Builder
    public record OAuthLoginUri(
        OAuthType type,
        URI loginUri
    ) {

        public static OAuthLoginUri of(OAuthProvider provider) {
            return OAuthLoginUri.builder()
                .type(provider.getType())
                .loginUri(provider.getLoginUri())
                .build();
        }
    }
}
