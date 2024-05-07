package com.taste.zip.tastezip.dto;

public record CafeteriaResponse(
    Long cafeteriaId,
    String cafeteriaName,
    String streetAddress,
    int videoCnt // 식당에 등록된 영상 개수
    ) {

}