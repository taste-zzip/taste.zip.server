package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.entity.enumeration.AccountType;
import jakarta.validation.constraints.NotNull;

public record AccountUpdateRequest(
    @NotNull(message = "{field.not-null}")
    String nickname,
    String bio,
    String profileImage,
    @NotNull(message = "{field.not-null}")
    AccountType type
) {

}
