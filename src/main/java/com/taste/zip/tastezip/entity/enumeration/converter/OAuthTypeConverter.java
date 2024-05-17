package com.taste.zip.tastezip.entity.enumeration.converter;

import com.taste.zip.tastezip.entity.enumeration.AdminConfigType;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class OAuthTypeConverter implements AttributeConverter<OAuthType, String> {

    @Override
    public String convertToDatabaseColumn(OAuthType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public OAuthType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return OAuthType.valueOf(dbData);
    }
}
