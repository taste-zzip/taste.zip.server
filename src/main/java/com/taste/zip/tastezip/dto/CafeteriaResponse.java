package com.taste.zip.tastezip.dto;

public record CafeteriaResponse(
    Long cafeteriaId,
    String cafeteriaName,
    String streetAddress) { // 등록된 영상 개수도 불러와야 함.

}