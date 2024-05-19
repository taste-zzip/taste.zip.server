package com.taste.zip.tastezip.entity.enumeration.converter;

import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType;
import com.taste.zip.tastezip.entity.enumeration.AdminConfigType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AdminConfigTypeConverter implements AttributeConverter<AdminConfigType, String> {

    @Override
    public String convertToDatabaseColumn(AdminConfigType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public AdminConfigType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return AdminConfigType.valueOf(dbData);
    }
}
