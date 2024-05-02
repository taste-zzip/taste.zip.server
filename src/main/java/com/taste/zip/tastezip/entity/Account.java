package com.taste.zip.tastezip.entity;

import com.taste.zip.tastezip.entity.enumeration.NameConverter;
import com.taste.zip.tastezip.entity.enumeration.AccountType;
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
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nickname used for service
     */
    @Column
    private String nickname;

    /**
     * Brief introduction written by user
     */
    @Column
    private String bio;

    /**
     * Profile image
     * Default is image that OAuth 2.0 provider gives
     */
    @Column
    private String profileImage;

    /**
     * Indicates which user's type is
     */
    @Column
    @Convert(converter = NameConverter.class)
    private AccountType type;
}