package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.entity.AccountVideoMapping;
import com.taste.zip.tastezip.entity.Video;
import com.taste.zip.tastezip.entity.enumeration.VideoPlatform;
import com.taste.zip.tastezip.entity.enumeration.VideoStatus;

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
        int viewCount
) {
        public static VideoResponse from(Video video, com.google.api.services.youtube.model.VideoSnippet snippet, com.google.api.services.youtube.model.VideoStatistics statistics) {
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
                statistics == null ? 0 : statistics.getViewCount().intValue()
            );
        }

}
