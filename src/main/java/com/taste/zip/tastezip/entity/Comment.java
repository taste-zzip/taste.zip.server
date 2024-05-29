package com.taste.zip.tastezip.entity;

import com.taste.zip.tastezip.entity.AccountCafeteriaMapping.AccountCafeteriaMappingBuilder;
import com.taste.zip.tastezip.entity.enumeration.AccountCafeteriaMappingType;
import jakarta.persistence.Column;
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
public class Comment extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cafeteria cafeteria;

    public static CommentBuilder builder(String content, Account account, Cafeteria cafeteria) {
        return hiddenBuilder()
            .content(content)
            .account(account)
            .cafeteria(cafeteria);
    }
}
