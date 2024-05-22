package com.taste.zip.tastezip.entity.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountVideoMappingType {

    /**
     * 좋아요
     */
    LIKE(Like.class),
    /**
     * 트로피 점수 (이상형 월드컵)
     */
    TROPHY(Trophy.class),
    /**
     * 평점
     */
    STAR(Double.class);

    public enum Like {
        LIKE

        /**
         * TODO 싫어요 생기면 DISLIKE 추가
         */
    }

    public enum Trophy {
        TROPHY
    }

    private final Class<?> valueClassType;
}
