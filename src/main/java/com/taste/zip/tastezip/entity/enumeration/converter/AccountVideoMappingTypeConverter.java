package com.taste.zip.tastezip.entity.enumeration.converter;

import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AccountVideoMappingTypeConverter implements AttributeConverter<AccountVideoMappingType, String> {

    @Override
    public String convertToDatabaseColumn(AccountVideoMappingType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public AccountVideoMappingType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return AccountVideoMappingType.valueOf(dbData);
    }
}
