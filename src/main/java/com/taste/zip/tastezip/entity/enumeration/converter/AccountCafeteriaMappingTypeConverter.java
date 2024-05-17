package com.taste.zip.tastezip.entity.enumeration.converter;

import com.taste.zip.tastezip.entity.enumeration.AccountCafeteriaMappingType;
import com.taste.zip.tastezip.entity.enumeration.AccountType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AccountCafeteriaMappingTypeConverter implements AttributeConverter<AccountCafeteriaMappingType, String> {

    @Override
    public String convertToDatabaseColumn(AccountCafeteriaMappingType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public AccountCafeteriaMappingType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return AccountCafeteriaMappingType.valueOf(dbData);
    }
}
