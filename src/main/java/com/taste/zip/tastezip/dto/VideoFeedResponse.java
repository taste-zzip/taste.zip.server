package com.taste.zip.tastezip.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.entity.AccountVideoMapping;
import com.taste.zip.tastezip.entity.Cafeteria;
import com.taste.zip.tastezip.entity.Video;
import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

public record VideoFeedResponse(
    List<Feed> feedList

) {

    @Builder
    public record Feed(
        @JsonIgnoreProperties(value = { "cafeteria" })
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
        Integer TROPHY,
        Integer SCORE
    ) {
        public static AccountMapping of(List<AccountVideoMapping> mappingList) {
            AccountVideoMappingType.Like like = null;
            Integer trophy = null;
            Integer score = null;

            for (AccountVideoMapping mapping : mappingList) {
                switch (mapping.getType()) {
                    case LIKE -> {
                        like = AccountVideoMappingType.Like.valueOf(mapping.getScore());
                    }
                    case TROPHY, SCORE -> {
                        trophy = Integer.parseInt(mapping.getScore());
                    }
                }
            }

            return AccountMapping.builder()
                .LIKE(like)
                .TROPHY(trophy)
                .SCORE(score)
                .build();
        }
    }

    @Builder
    public record YoutubeVideo(
        String publishedAt,
        String channelId,
        String title,
        String description,
        String thumbnail,
        Long viewCount,
        Long likeCount,
        Long favoriteCount,
        Long commentCount
    ) {
        public static YoutubeVideo of(com.google.api.services.youtube.model.VideoSnippet snippet, com.google.api.services.youtube.model.VideoStatistics statistics) {
            return VideoFeedResponse.YoutubeVideo.builder()
                .publishedAt(snippet.getPublishedAt().toString())
                .channelId(snippet.getChannelId())
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .thumbnail(snippet.getThumbnails().getMaxres().getUrl())
                .viewCount(statistics.getViewCount().longValue())
                .likeCount(statistics.getLikeCount().longValue())
                .favoriteCount(statistics.getFavoriteCount().longValue())
                .commentCount(statistics.getCommentCount().longValue())
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
                .publishedAt(snippet.getPublishedAt().toString())
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .customId(snippet.getCustomUrl())
                .thumbnail(snippet.getThumbnails().getDefault().getUrl())
                .viewCount(statistics.getViewCount().longValue())
                .subscriberCount(statistics.getSubscriberCount().longValue())
                .videoCount(statistics.getVideoCount().longValue())
                .channelUrl(youtubeUrl + snippet.getCustomUrl())
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
