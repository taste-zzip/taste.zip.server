package com.taste.zip.tastezip.entity.enumeration.converter;

import com.taste.zip.tastezip.entity.enumeration.AccountCafeteriaMappingType;
import com.taste.zip.tastezip.entity.enumeration.AccountConfigType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AccountConfigTypeConverter implements AttributeConverter<AccountConfigType, String> {

    @Override
    public String convertToDatabaseColumn(AccountConfigType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public AccountConfigType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return AccountConfigType.valueOf(dbData);
    }
}
