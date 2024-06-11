package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.entity.Cafeteria;
import com.taste.zip.tastezip.entity.Video;
import java.util.List;

public record YoutubeLikeCafeteriaUpdateResponse(
    @JsonIgnoreProperties(value = { "videos", "videoCnt", "comments", "commentCnt", "hibernateLazyInitializer", "handler" })
    List<Cafeteria> likedCafeteria,

    @JsonIgnoreProperties(value = { "cafeteria", "accountVideoMappings" })
    List<Video> likedVideo
) {

}
