package com.taste.zip.tastezip.entity.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdminConfigType {
    /**
     * ACCESS TOKEN 유효 시간
     */
    ACCESS_TOKEN_DURATION(Integer.class),
    /**
     * REFRESH TOKEN 유효 시간
     */
    REFRESH_TOKEN_DURATION(Integer.class);

    private final Class<?> valueClassType;
}
