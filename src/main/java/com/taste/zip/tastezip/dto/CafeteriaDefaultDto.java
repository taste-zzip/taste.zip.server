package com.taste.zip.tastezip.dto;

import com.taste.zip.tastezip.entity.Cafeteria;

public record CafeteriaDefaultDto(
        Long id,
        String name
) {
    public static CafeteriaDefaultDto from(Cafeteria cafeteria) {
        return new CafeteriaDefaultDto(
                cafeteria.getId(),
                cafeteria.getName()
        );
    }

}
