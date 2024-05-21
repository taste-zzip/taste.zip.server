package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.entity.Cafeteria;

import java.util.List;
import java.util.stream.Collectors;

public record CafeteriaDetailResponse (
        Long id,
        String name,
        String type,
        String address,
        String latitude,
        String longitude,
        int videoCnt,
        List<VideoResponse> videos
){
    public static CafeteriaDetailResponse from(Cafeteria cafeteria) {

        List<VideoResponse> videoResponses = cafeteria.getVideos().stream()
                .map(VideoResponse::from)
                .collect(Collectors.toList());

        return new CafeteriaDetailResponse(
                cafeteria.getId(),
                cafeteria.getName(),
                cafeteria.getType(),
                cafeteria.getStreetAddress(),
                cafeteria.getLatitude(),
                cafeteria.getLongitude(),
                cafeteria.getVideoCnt(),
                videoResponses
        );
    }


}
