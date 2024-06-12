package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.entity.Cafeteria;

public record CafeteriaResponse(
    Long id,
    String name,
    String type,
    String streetAddress,
    String landAddress,
    String city,
    String district,
    String neighborhood,
    String latitude,
    String longitude,
    int videoCnt, // 식당에 등록된 영상 개수
    int commentCnt
    ) {
    public static CafeteriaResponse from(Cafeteria cafeteria) {
        return new CafeteriaResponse(
            cafeteria.getId(),
            cafeteria.getName(),
            cafeteria.getType(),
            cafeteria.getStreetAddress(),
            cafeteria.getLandAddress(),
            cafeteria.getCity(),
            cafeteria.getDistrict(),
            cafeteria.getNeighborhood(),
            cafeteria.getLatitude(),
            cafeteria.getLongitude(),
            cafeteria.getVideoCnt(),
            cafeteria.getCommentCnt()
        );
    }
}
