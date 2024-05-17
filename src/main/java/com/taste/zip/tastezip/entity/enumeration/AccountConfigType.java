package com.taste.zip.tastezip.entity.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AccountConfigType {
    /**
     * 기본 이용 약관 동의 여부
     */
    TERM_OF_USE_AGREEMENT(Boolean.class),
    /**
     * 위치 기반 서비스 이용 동의 여부
     */
    TERM_OF_GPS_AGREEMENT(Boolean.class),
    /**
     * 마케팅 정보 수신 동의 여부
     */
    MARKETING_MESSAGE_AGREEMENT(Boolean.class);

    private final Class<?> valueClassType;
}
