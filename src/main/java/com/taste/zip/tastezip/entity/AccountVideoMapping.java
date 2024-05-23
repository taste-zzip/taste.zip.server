package com.taste.zip.tastezip.entity;

import com.taste.zip.tastezip.entity.AccountCafeteriaMapping.AccountCafeteriaMappingBuilder;
import com.taste.zip.tastezip.entity.enumeration.AccountCafeteriaMappingType;
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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;
import java.util.stream.Collectors;

@Builder(builderMethodName = "hiddenBuilder")
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

    @Column(nullable = true) // LIKE, TROPHY의 경우 score는 NULL
    private Double score; // string type을 Double 변경 (nullable 위해 Double인 wrapper class로 선언)

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.SET_NULL)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Video video;

    public double getAverageScore() {

        List<AccountVideoMapping> scoreMappings = video.getAccountVideoMappings()
                .stream()
                .filter(mapping -> mapping.getType() == AccountVideoMappingType.STAR) // SCORE 타입 매핑 값만 filter
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
        long trophyCount = video.getAccountVideoMappings()
                .stream()
                .filter(mapping -> mapping.getType() == AccountVideoMappingType.TROPHY)
                .count();

        return (int) trophyCount;
    }

    public static AccountVideoMappingBuilder builder(AccountVideoMappingType type, Account account, Video video) {
        return hiddenBuilder()
            .type(type)
            .account(account)
            .video(video);
    }
}
