package com.taste.zip.tastezip.dto;

import jakarta.validation.constraints.NotNull;

public record CafeteriaCommentCreateRequest(
    @NotNull(message = "{field.not-null}")
    String content
) {

}
