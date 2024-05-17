package com.taste.zip.tastezip.entity;

import com.taste.zip.tastezip.entity.enumeration.AdminConfigType;
import com.taste.zip.tastezip.entity.enumeration.converter.AccountTypeConverter;
import com.taste.zip.tastezip.entity.enumeration.converter.AdminConfigTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminConfig extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Convert(converter = AdminConfigTypeConverter.class)
    private AdminConfigType type;

    @Column
    private String value;
}
