package com.taste.zip.tastezip.entity.enumeration;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class NameConverter<T extends Enum<T>> implements AttributeConverter<Enum, String> {

    @Override
    public String convertToDatabaseColumn(Enum attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.name();
    }

    @Override
    public Enum convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return Enum.class.cast(dbData);
    }
}
