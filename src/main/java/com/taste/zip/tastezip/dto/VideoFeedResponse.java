package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.entity.AccountVideoMapping;
import com.taste.zip.tastezip.entity.Cafeteria;
import com.taste.zip.tastezip.entity.Video;
import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType;
import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType.Like;
import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType.Trophy;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

public record VideoFeedResponse(
    List<Feed> feedList

) {

    @Builder
    public record Feed(
        @JsonIgnoreProperties(value = { "cafeteria", "accountVideoMappings" })
        Video video,
        @JsonIgnoreProperties(value = { "videos", "videoCnt", "hibernateLazyInitializer", "handler" })
        Cafeteria cafeteria,
        YoutubeVideo youtubeVideo,
        YoutubeChannel youtubeChannel,
        AccountMapping accountVideoMapping,
        Statistic statistic
    ) {

    }


    /**
     * @see com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType
     * 필드 이름 잘 맞출 것, 오타 조심!
     */
    @Builder(access = AccessLevel.PRIVATE)
    public record AccountMapping(
        AccountVideoMappingType.Like LIKE,
        AccountVideoMappingType.Trophy TROPHY,
        Double STAR
    ) {
        public static AccountMapping of(List<AccountVideoMapping> mappingList) {
            AccountVideoMappingType.Like like = null;
            AccountVideoMappingType.Trophy trophy = null;
            Double star = null;

            for (AccountVideoMapping mapping : mappingList) {
                switch (mapping.getType()) {
                    case LIKE -> {
                        like = Like.LIKE;
                    }
                    case TROPHY -> {
                        trophy = Trophy.TROPHY;
                    }
                    case STAR -> {
                        star = mapping.getScore();
                    }
                }
            }

            return AccountMapping.builder()
                .LIKE(like)
                .TROPHY(trophy)
                .STAR(star)
                .build();
        }
    }

    @Builder
    public record YoutubeVideo(
        String publishedAt,
        String channelId,
        String title,
        String description,
        Long viewCount,
        Long likeCount,
        Long favoriteCount,
        Long commentCount
    ) {
        public static YoutubeVideo of(com.google.api.services.youtube.model.VideoSnippet snippet, com.google.api.services.youtube.model.VideoStatistics statistics) {
            return VideoFeedResponse.YoutubeVideo.builder()
                .publishedAt(snippet == null ? null : snippet.getPublishedAt().toString())
                .channelId(snippet == null ? null : snippet.getChannelId())
                .title(snippet == null ? null : snippet.getTitle())
                .description(snippet == null ? null : snippet.getDescription())
                .viewCount(statistics == null ? null : statistics.getViewCount().longValue())
                .likeCount(statistics == null ? null : statistics.getLikeCount().longValue())
                .favoriteCount(statistics == null ? null : statistics.getFavoriteCount().longValue())
                .commentCount(statistics == null ? null : statistics.getCommentCount().longValue())
                .build();
        }
    }

    @Builder
    public record YoutubeChannel(
        String publishedAt,
        String title,
        String description,
        String customId,
        String thumbnail,
        Long viewCount,
        Long subscriberCount,
        Long videoCount,
        String channelUrl
    ) {
        public static YoutubeChannel of(com.google.api.services.youtube.model.ChannelSnippet snippet, com.google.api.services.youtube.model.ChannelStatistics statistics) {
            String youtubeUrl = "https://www.youtube.com/";

            return YoutubeChannel.builder()
                .publishedAt(snippet == null ? null : snippet.getPublishedAt().toString())
                .title(snippet == null ? null : snippet.getTitle())
                .description(snippet == null ? null : snippet.getDescription())
                .customId(snippet == null ? null : snippet.getCustomUrl())
                .thumbnail(snippet == null ? null : snippet.getThumbnails().getStandard().getUrl())
                .viewCount(statistics == null ? null : statistics.getViewCount().longValue())
                .subscriberCount(statistics == null ? null : statistics.getSubscriberCount().longValue())
                .videoCount(statistics == null ? null : statistics.getVideoCount().longValue())
                .channelUrl(snippet == null ? null : youtubeUrl + snippet.getCustomUrl())
                .build();
        }
    }

    @Builder
    public record Statistic(
        Long videoLikeCount,
        Long videoTrophyCount,
        Long cafeteriaVideoCount
    ) {

    }
}
