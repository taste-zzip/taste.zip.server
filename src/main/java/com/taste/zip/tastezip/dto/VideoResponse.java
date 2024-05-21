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
        public static VideoResponse from(Video video) {

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
                    null, null, null, 0 // youtube에서 불러오는 data
//                    video.getVideoUrl(),
//                    video.getThumbnailUrl(),
//                    video.getTitle(),
//                    video.getViewCount()
            );
        }

}