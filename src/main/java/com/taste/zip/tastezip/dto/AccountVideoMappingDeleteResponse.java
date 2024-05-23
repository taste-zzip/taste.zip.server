package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingDeleteResponse.AccountCafeteriaMappingDeleteResponseBuilder;
import com.taste.zip.tastezip.entity.AccountCafeteriaMapping;
import com.taste.zip.tastezip.entity.AccountVideoMapping;
import lombok.Builder;

@Builder(builderMethodName = "hiddenBuilder")
public record AccountVideoMappingDeleteResponse(
    @JsonIgnoreProperties(value = { "account", "video" })
    AccountVideoMapping mapping
) {

    public static AccountVideoMappingDeleteResponseBuilder builder(AccountVideoMapping mapping) {
        return hiddenBuilder()
            .mapping(mapping);
    }
}
