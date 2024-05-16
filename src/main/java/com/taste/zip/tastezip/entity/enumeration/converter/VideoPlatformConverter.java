package com.taste.zip.tastezip.entity.enumeration.converter;

import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import com.taste.zip.tastezip.entity.enumeration.VideoPlatform;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class VideoPlatformConverter implements AttributeConverter<VideoPlatform, String> {

    @Override
    public String convertToDatabaseColumn(VideoPlatform attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public VideoPlatform convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return VideoPlatform.valueOf(dbData);
    }
}
