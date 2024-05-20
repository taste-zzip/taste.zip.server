package com.taste.zip.tastezip.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.dto.VideoFeedResponse;
import com.taste.zip.tastezip.entity.AccountVideoMapping;
import com.taste.zip.tastezip.entity.Video;
import com.taste.zip.tastezip.repository.AccountRepository;
import com.taste.zip.tastezip.repository.AccountVideoMappingRepository;
import com.taste.zip.tastezip.repository.VideoRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class VideoService {

    private final AccountRepository accountRepository;
    private final VideoRepository videoRepository;
    private final AccountVideoMappingRepository accountVideoMappingRepository;
    private final MessageSource messageSource;

    public VideoFeedResponse getVideoFeed(long size, TokenDetail tokenDetail) {
        if (!accountRepository.existsById(tokenDetail.userId())) {
            final String message = messageSource.getMessage("account.find.not-exist",
                new Object[]{tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }

        final List<Video> videos = videoRepository.findTopByRandomly(size, tokenDetail.userId());
        List<VideoFeedResponse.Feed> feedResult = new ArrayList<>();

        for (Video video : videos) {
            final List<AccountVideoMapping> mappingList = accountVideoMappingRepository.findAllByAccount_IdAndVideoId(
                tokenDetail.userId(), video.getId());

            feedResult.add(new VideoFeedResponse.Feed(
                video, video.getCafeteria(), VideoFeedResponse.AccountMapping.of(mappingList))
            );
        }

        return new VideoFeedResponse(feedResult);
    }
}
