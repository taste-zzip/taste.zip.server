package com.taste.zip.tastezip.entity;

import com.taste.zip.tastezip.entity.enumeration.converter.AccountTypeConverter;
import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType;
import com.taste.zip.tastezip.entity.enumeration.converter.AccountVideoMappingTypeConverter;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountVideoMapping extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Convert(converter = AccountVideoMappingTypeConverter.class)
    private AccountVideoMappingType type;

    @Column
    private double score; // string type을 double로 변경

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Video video;

    public double getAverageScore() {

        List<AccountVideoMapping> scoreMappings = video.getAccountVideoMappings()
                .stream()
                .filter(mapping -> mapping.getType() == AccountVideoMappingType.SCORE) // SCORE 타입 매핑 값만 filter
                .toList();

        if (scoreMappings.isEmpty()) {
            return 0.0; // SCORE 타입의 값이 없으면 0.0을 반환
        }

        double totalScore = scoreMappings.stream()
                .mapToDouble(AccountVideoMapping::getScore)
                .sum();

        return totalScore / scoreMappings.size();
    }

    public int getTotalTrophyCount() {
        List<AccountVideoMapping> trophyMappings = video.getAccountVideoMappings()
                .stream()
                .filter(mapping -> mapping.getType() == AccountVideoMappingType.TROPHY) // TROPHY 타입 매핑 값만 filter
                .toList();

        return trophyMappings.size();
    }

}
