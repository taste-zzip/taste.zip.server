package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.entity.Cafeteria;

public record CafeteriaResponse(
    Long cafeteriaId,
    String cafeteriaName,
    String streetAddress,
    int videoCnt // 식당에 등록된 영상 개수
    ) {
    public static CafeteriaResponse from(Cafeteria cafeteria) {
        return new CafeteriaResponse(
            cafeteria.getId(),
            cafeteria.getName(),
            cafeteria.getStreetAddress(),
            cafeteria.getVideoCnt()
        );
    }
}