package com.taste.zip.tastezip.entity;

import com.taste.zip.tastezip.entity.enumeration.converter.AccountTypeConverter;
import com.taste.zip.tastezip.entity.enumeration.VideoPlatform;
import com.taste.zip.tastezip.entity.enumeration.VideoStatus;
import com.taste.zip.tastezip.entity.enumeration.converter.VideoPlatformConverter;
import com.taste.zip.tastezip.entity.enumeration.converter.VideoStatusConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.List;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    uniqueConstraints = {
        @UniqueConstraint(
            name = "platform__videoPk__unique",
            columnNames = {"platform", "videoPk"}
        )
    }
)
public class Video extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Cafeteria cafeteria;

    @Column
    @Convert(converter = VideoPlatformConverter.class)
    private VideoPlatform platform;

    @Column
    private String videoPk;

    @Column
    @Convert(converter = VideoStatusConverter.class)
    private VideoStatus status;

    // Video에서 AccountVideoMapping 일대다 조회
    @OneToMany(mappedBy = "video", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<AccountVideoMapping> accountVideoMappings;

}
