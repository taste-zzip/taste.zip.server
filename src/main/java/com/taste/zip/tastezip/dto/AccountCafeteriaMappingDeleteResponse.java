package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.entity.AccountCafeteriaMapping;
import lombok.Builder;

@Builder(builderMethodName = "hiddenBuilder")
public record AccountCafeteriaMappingDeleteResponse(
    @JsonIgnoreProperties(value = { "account", "cafeteria" })
    AccountCafeteriaMapping mapping
) {

    public static AccountCafeteriaMappingDeleteResponseBuilder builder(AccountCafeteriaMapping mapping) {
        return hiddenBuilder()
            .mapping(mapping);
    }
}
