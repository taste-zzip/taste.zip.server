package com.taste.zip.tastezip.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Cafeteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String type;

    @Column
    private String streetAddress;

    @Column
    private String landAddress;

    @Column
    private String city;

    @Column
    private String district;

    @Column
    private String neighborhood;

    @Column
    private String latitude;

    @Column
    private String longitude;

    @OneToMany(mappedBy = "cafeteria", fetch = FetchType.LAZY)
    private List<Video> videos;

    @OneToMany(mappedBy = "cafeteria", fetch = FetchType.LAZY)
    private List<Comment> comments;

    public int getVideoCnt() {
        return videos != null ? videos.size() : 0;
    }

    public int getCommentCnt() {
        return comments != null ? comments.size() : 0;
    }

}
