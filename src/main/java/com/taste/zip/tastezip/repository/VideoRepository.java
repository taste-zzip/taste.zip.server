package com.taste.zip.tastezip.repository;

import com.taste.zip.tastezip.entity.Video;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface VideoRepository extends JpaRepository<Video, Long> {

    @Query("SELECT v FROM Video v ORDER BY FUNCTION('RANDOM') LIMIT ?1")
    List<Video> findTopByRandomly(long size, Long accountId);
}
