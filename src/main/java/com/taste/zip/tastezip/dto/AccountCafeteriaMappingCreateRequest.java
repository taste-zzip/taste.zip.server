package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.entity.enumeration.AccountCafeteriaMappingType;
import jakarta.validation.constraints.NotNull;

public record AccountCafeteriaMappingCreateRequest(
    @NotNull(message = "{field.not-null}")
    Long cafeteriaId,
    @NotNull(message = "{field.not-null}")
    AccountCafeteriaMappingType type
) {

}
