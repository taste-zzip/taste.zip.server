package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.entity.enumeration.AccountCafeteriaMappingType;
import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType;
import jakarta.validation.constraints.NotNull;

public record AccountVideoMappingDeleteRequest(
    @NotNull(message = "{field.not-null}")
    Long videoId,
    @NotNull(message = "{field.not-null}")
    AccountVideoMappingType type
) {

}
