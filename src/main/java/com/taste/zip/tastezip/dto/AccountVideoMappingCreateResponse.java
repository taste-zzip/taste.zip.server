package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.entity.AccountCafeteriaMapping;
import com.taste.zip.tastezip.entity.AccountVideoMapping;
import lombok.Builder;

@Builder(builderMethodName = "hiddenBuilder")
public record AccountVideoMappingCreateResponse(
    @JsonIgnoreProperties(value = { "account", "video", "averageScore", "totalTrophyCount" })
    AccountVideoMapping videoMapping,
    @JsonIgnoreProperties(value = { "account", "cafeteria" })
    AccountCafeteriaMapping cafeteriaMapping,
    boolean youtubeLikeSuccess
) {

    public static AccountVideoMappingCreateResponseBuilder builder(AccountVideoMapping videoMapping) {
        return hiddenBuilder()
            .videoMapping(videoMapping);
    }
}
