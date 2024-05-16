package com.taste.zip.tastezip.entity;

import com.taste.zip.tastezip.entity.enumeration.converter.AccountConfigTypeConverter;
import com.taste.zip.tastezip.entity.enumeration.converter.AccountTypeConverter;
import com.taste.zip.tastezip.entity.enumeration.AccountConfigType;
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

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(builderMethodName = "hiddenBuilder")
public class AccountConfig extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    @Column
    @Convert(converter = AccountConfigTypeConverter.class)
    private AccountConfigType type;

    @Column(length = 1047)
    private String value;

    public static AccountConfigBuilder builder(Account account) {
        return hiddenBuilder()
            .account(account);
    }
}
