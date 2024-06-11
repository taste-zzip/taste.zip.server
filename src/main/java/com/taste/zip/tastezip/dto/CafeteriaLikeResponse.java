package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.entity.Cafeteria;
import com.taste.zip.tastezip.entity.Video;
import java.util.List;

public record CafeteriaLikeResponse(
    List<CafeteriaLike> cafeteriaList
) {
    public record CafeteriaLike(
        @JsonIgnoreProperties(value = { "videos", "videoCnt", "comments", "commentCnt", "hibernateLazyInitializer", "handler" })
        Cafeteria cafeteria,
        Long videoCnt,
        Long commentCnt,
        List<CafeteriaVideo> videoList
    ) {
    }

    public record CafeteriaVideo(
        @JsonIgnoreProperties(value = { "cafeteria", "accountVideoMappings" })
        Video video,
        VideoFeedResponse.YoutubeVideo youtubeVideo,
        Statistic statistic
    ) {

    }

    public record Statistic(
        Long videoLikeCount,
        Long videoTrophyCount
    ) {

    }
}
