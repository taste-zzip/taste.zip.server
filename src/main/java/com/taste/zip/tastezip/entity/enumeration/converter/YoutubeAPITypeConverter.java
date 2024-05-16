package com.taste.zip.tastezip.entity.enumeration.converter;

import com.taste.zip.tastezip.entity.enumeration.VideoStatus;
import com.taste.zip.tastezip.entity.enumeration.YoutubeAPIType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class YoutubeAPITypeConverter implements AttributeConverter<YoutubeAPIType, String> {

    @Override
    public String convertToDatabaseColumn(YoutubeAPIType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public YoutubeAPIType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return YoutubeAPIType.valueOf(dbData);
    }
}
