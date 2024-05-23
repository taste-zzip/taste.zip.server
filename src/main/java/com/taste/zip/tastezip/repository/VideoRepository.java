package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.Video;
import com.taste.zip.tastezip.entity.enumeration.VideoPlatform;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT v FROM Video v ORDER BY FUNCTION('RANDOM') LIMIT ?1")
    List<Video> findTopByRandomly(long size, Long accountId);

    Optional<Video> findByPlatformAndVideoPk(VideoPlatform platform, String videoPk);
}
