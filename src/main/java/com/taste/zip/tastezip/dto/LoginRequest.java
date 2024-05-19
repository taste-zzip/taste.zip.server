package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
    @NotNull(message = "{field.not-null}")
    OAuthType type,
    @NotNull(message = "{field.not-null}")
    String code
) {

}
