package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record AccountVideoMappingCreateRequest(
    @NotNull(message = "{field.not-null}")
    Long videoId,
    @NotNull(message = "{field.not-null}")
    AccountVideoMappingType type,
    @Nullable
    Double score
) {

}
