package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.entity.Comment;

public record CafeteriaCommentDeleteResponse(
    @JsonIgnoreProperties(value = { "account", "cafeteria" })
    Comment comment
) {

}
