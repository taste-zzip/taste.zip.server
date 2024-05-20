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
    public record Feed(
        @JsonIgnoreProperties(value = { "cafeteria" })
        Video video,
        @JsonIgnoreProperties(value = { "videos", "videoCnt", "hibernateLazyInitializer", "handler" })
        Cafeteria cafeteria,
        AccountMapping accountVideoMapping
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
}
