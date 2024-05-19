package com.taste.zip.tastezip.entity;

import com.taste.zip.tastezip.entity.enumeration.converter.AccountTypeConverter;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import com.taste.zip.tastezip.entity.enumeration.converter.OAuthTypeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
@Table(
    name = "account_oauth",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "type__oauthPk__unique",
            columnNames = {"type", "oauthPk"}
        )
    }
)
@Builder(builderMethodName = "hiddenBuilder")
public class AccountOAuth extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Account account;

    @Column
    @Convert(converter = OAuthTypeConverter.class)
    private OAuthType type;

    @Column
    private String oauthPk;

    @Column
    private String accessToken;

    @Column
    private String refreshToken;

    @Column
    private String email;

    @Column
    private String profileImage;

    @Column(length = 8191)
    private String rawData;

    public static AccountOAuthBuilder builder(Account account, OAuthType type, String oauthPk) {
        return hiddenBuilder()
            .account(account)
            .type(type)
            .oauthPk(oauthPk);
    }
}
