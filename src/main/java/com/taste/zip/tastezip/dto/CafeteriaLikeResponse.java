package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.entity.Cafeteria;
import java.util.List;

public record CafeteriaLikeResponse(
    @JsonIgnoreProperties(value = { "videos", "videoCnt", "hibernateLazyInitializer", "handler" })
    List<Cafeteria> cafeteriaList
) {

}
