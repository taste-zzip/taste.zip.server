package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.entity.Comment;
import java.util.List;
import org.springframework.data.domain.Page;

public record CafeteriaCommentListResponse(
    Page<CommentItem> commentList
) {
    public record CommentItem(
        @JsonIgnoreProperties(value = { "account", "cafeteria", "hibernateLazyInitializer", "handler" })
        Comment comment,
        @JsonIgnoreProperties(value = { "hibernateLazyInitializer", "handler" })
        Account account
    ) {

    }
}
