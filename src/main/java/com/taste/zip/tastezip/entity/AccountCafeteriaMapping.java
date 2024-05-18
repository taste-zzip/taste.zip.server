package com.taste.zip.tastezip.entity;

import com.taste.zip.tastezip.entity.Account.AccountBuilder;
import com.taste.zip.tastezip.entity.enumeration.AccountType;
import com.taste.zip.tastezip.entity.enumeration.converter.AccountCafeteriaMappingTypeConverter;
import com.taste.zip.tastezip.entity.enumeration.converter.AccountTypeConverter;
import com.taste.zip.tastezip.entity.enumeration.AccountCafeteriaMappingType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Builder(builderMethodName = "hiddenBuilder")
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountCafeteriaMapping extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Convert(converter = AccountCafeteriaMappingTypeConverter.class)
    private AccountCafeteriaMappingType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cafeteria cafeteria;

    public static AccountCafeteriaMappingBuilder builder(AccountCafeteriaMappingType type, Account account, Cafeteria cafeteria) {
        return hiddenBuilder()
            .type(type)
            .account(account)
            .cafeteria(cafeteria);
    }
}
