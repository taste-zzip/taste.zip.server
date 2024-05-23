package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.dto.VideoFeedResponse.AccountMapping;
import com.taste.zip.tastezip.entity.AccountVideoMapping;
import com.taste.zip.tastezip.entity.Video;
import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType;
import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType.Like;
import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType.Trophy;
import com.taste.zip.tastezip.entity.enumeration.VideoPlatform;
import com.taste.zip.tastezip.entity.enumeration.VideoStatus;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;

public record VideoResponse(
        Long id,
        VideoPlatform platform,
        String videoPk,
        VideoStatus status,
        double starAverage,
        int trophyCount,
        String videoUrl,
        String thumbnailUrl,
        String title,
        int viewCount,
        AccountMapping accountVideoMapping
) {
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
        public static VideoResponse.AccountMapping of(List<AccountVideoMapping> mappingList) {
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

            return VideoResponse.AccountMapping.builder()
                .LIKE(like)
                .TROPHY(trophy)
                .STAR(star)
                .build();
        }
    }

        public static VideoResponse from(Video video, com.google.api.services.youtube.model.VideoSnippet snippet, com.google.api.services.youtube.model.VideoStatistics statistics, VideoResponse.AccountMapping mapping) {
            String youtubeUrl = "https://www.youtube.com/watch?v=";

            double starAverage = video.getAccountVideoMappings().stream()
                    .mapToDouble(AccountVideoMapping::getAverageScore)
                    .average()
                    .orElse(0.0);

            int trophyCount = (int) video.getAccountVideoMappings().stream()
                    .map(AccountVideoMapping::getTotalTrophyCount)
                    .count();

            return new VideoResponse(
                video.getId(),
                video.getPlatform(),
                video.getVideoPk(),
                video.getStatus(),
                starAverage,
                trophyCount,
                youtubeUrl + video.getVideoPk(),
                snippet == null ? null : snippet.getThumbnails().getStandard().getUrl(),
                snippet == null ? null : snippet.getTitle(),
                statistics == null ? 0 : statistics.getViewCount().intValue(),
                mapping
            );
        }

}
