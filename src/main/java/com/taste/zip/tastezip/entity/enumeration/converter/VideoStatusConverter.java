package com.taste.zip.tastezip.entity.enumeration.converter;

import com.taste.zip.tastezip.entity.enumeration.VideoPlatform;
import com.taste.zip.tastezip.entity.enumeration.VideoStatus;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class VideoStatusConverter implements AttributeConverter<VideoStatus, String> {

    @Override
    public String convertToDatabaseColumn(VideoStatus attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public VideoStatus convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return VideoStatus.valueOf(dbData);
    }
}
