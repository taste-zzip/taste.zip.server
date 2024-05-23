package com.taste.zip.tastezip.dto;

import jakarta.validation.constraints.NotNull;

public record YoutubeLikeCafeteriaUpdateRequest(
    @NotNull(message = "{field.not-null}")
    long size
) {

}
