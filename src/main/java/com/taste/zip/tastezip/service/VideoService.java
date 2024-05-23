package com.taste.zip.tastezip.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.oauth2.Oauth2;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import com.taste.zip.tastezip.auth.GoogleOAuthProvider;
import com.taste.zip.tastezip.auth.OAuthProvider;
import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingCreateResponse;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingDeleteRequest;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingDeleteResponse;
import com.taste.zip.tastezip.dto.AccountVideoMappingCreateRequest;
import com.taste.zip.tastezip.dto.AccountVideoMappingCreateResponse;
import com.taste.zip.tastezip.dto.AccountVideoMappingDeleteRequest;
import com.taste.zip.tastezip.dto.AccountVideoMappingDeleteResponse;
import com.taste.zip.tastezip.dto.VideoFeedResponse;
import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.entity.AccountCafeteriaMapping;
import com.taste.zip.tastezip.entity.AccountOAuth;
import com.taste.zip.tastezip.entity.AccountVideoMapping;
import com.taste.zip.tastezip.entity.Cafeteria;
import com.taste.zip.tastezip.entity.Video;
import com.taste.zip.tastezip.entity.enumeration.AccountCafeteriaMappingType;
import com.taste.zip.tastezip.entity.enumeration.AccountVideoMappingType;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import com.taste.zip.tastezip.entity.enumeration.VideoPlatform;
import com.taste.zip.tastezip.repository.AccountCafeteriaMappingRepository;
import com.taste.zip.tastezip.repository.AccountOAuthRepository;
import com.taste.zip.tastezip.repository.AccountRepository;
import com.taste.zip.tastezip.repository.AccountVideoMappingRepository;
import com.taste.zip.tastezip.repository.VideoRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
    private final AccountOAuthRepository accountOAuthRepository;
    private final VideoRepository videoRepository;
    private final AccountVideoMappingRepository accountVideoMappingRepository;
    private final AccountCafeteriaMappingRepository accountCafeteriaMappingRepository;
    private final MessageSource messageSource;
    private final GoogleOAuthProvider googleOAuthProvider;

    public VideoFeedResponse getVideoFeed(long size, TokenDetail tokenDetail) {
        if (!accountRepository.existsById(tokenDetail.userId())) {
            final String message = messageSource.getMessage("account.find.not-exist",
                new Object[]{tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }

        // Videos
        final List<Video> videos = videoRepository.findTopByRandomly(size, tokenDetail.userId());
        List<VideoFeedResponse.Feed> feedResult = new ArrayList<>();

        final Optional<AccountOAuth> accountGoogle = accountOAuthRepository.findByTypeAndAccount_Id(OAuthType.GOOGLE, tokenDetail.userId());
        YouTube youtubeClient = null;
        if (accountGoogle.isPresent()) {
            final AccountOAuth auth = accountGoogle.get();
            final TokenResponse tokenResponse = new TokenResponse()
                .setAccessToken(auth.getAccessToken())
                .setRefreshToken(auth.getRefreshToken())
                .setExpiresInSeconds(auth.getExpireSeconds())
                .setTokenType(auth.getTokenType())
                .setScope(auth.getScope());
            youtubeClient = googleOAuthProvider.createYoutubeClient(tokenResponse);
        }

        for (Video video : videos) {
            final List<AccountVideoMapping> mappingList = accountVideoMappingRepository.findAllByAccount_IdAndVideoId(
                tokenDetail.userId(), video.getId());

            // Youtube Video
            VideoListResponse videoResponse = null;
            ChannelListResponse channelResponse = null;
            if (youtubeClient != null && video.getPlatform() == VideoPlatform.YOUTUBE) {
                try {
                    videoResponse = youtubeClient.videos().list("id, snippet, statistics")
                        .setId(video.getVideoPk())
                        .execute();
                    if (!videoResponse.getItems().isEmpty()) {
                        channelResponse = youtubeClient.channels().list("id, snippet, statistics")
                            .setId(videoResponse.getItems().get(0).getSnippet().getChannelId())
                            .execute();
                    }
                } catch (IOException e) {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
                } catch (Exception e) {
                    videoResponse = null;
                    channelResponse = null;
                }
            }

            final Cafeteria cafeteria = video.getCafeteria();
            final long likeCount = accountVideoMappingRepository.countAllByVideoIdAndType(video.getId(), AccountVideoMappingType.LIKE);
            final long trophyCount = accountVideoMappingRepository.countAllByVideoIdAndType(video.getId(), AccountVideoMappingType.TROPHY);

            feedResult.add(VideoFeedResponse.Feed.builder()
                    .video(video)
                    .cafeteria(cafeteria)
                    .youtubeVideo(
                         videoResponse != null && !videoResponse.getItems().isEmpty() ?
                            VideoFeedResponse.YoutubeVideo.of(videoResponse.getItems().get(0).getSnippet(), videoResponse.getItems().get(0).getStatistics())
                            : null
                    )
                    .youtubeChannel(
                        channelResponse != null && !channelResponse.getItems().isEmpty() ?
                            VideoFeedResponse.YoutubeChannel.of(channelResponse.getItems().get(0).getSnippet(), channelResponse.getItems().get(0).getStatistics())
                            : null
                    )
                    .accountVideoMapping(VideoFeedResponse.AccountMapping.of(mappingList))
                    .statistic(VideoFeedResponse.Statistic.builder()
                        .videoLikeCount(likeCount)
                        .videoTrophyCount(trophyCount)
                        .cafeteriaVideoCount((long) cafeteria.getVideos().size())
                        .build())
                    .build()
            );
        }

        return new VideoFeedResponse(feedResult);
    }

    @Transactional
    public AccountVideoMappingCreateResponse saveInteract(AccountVideoMappingCreateRequest request, TokenDetail tokenDetail) {
        if (!accountRepository.existsById(tokenDetail.userId())) {
            final String message = messageSource.getMessage("account.find.not-exist", new Object[]{tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }
        if (!videoRepository.existsById(request.videoId())) {
            final String message = messageSource.getMessage("video.find.not-exist", new Object[]{request.videoId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }
        if (accountVideoMappingRepository.existsByTypeAndAccountIdAndVideoId(request.type(), tokenDetail.userId(), request.videoId())) {
            final String message = messageSource.getMessage("account.video.mapping.find.duplicated", new Object[]{request.type(), request.videoId(), tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.CONFLICT, message);
        }

        final Video video = videoRepository.findById(request.videoId()).get();
        final Account account = accountRepository.findById(tokenDetail.userId()).get();

        final AccountVideoMapping savedVideoMapping = accountVideoMappingRepository.save(
            AccountVideoMapping
                .builder(request.type(), account, video)
                .score(request.score())
                .build()
        );

        AccountCafeteriaMapping savedCafeteriaMapping = null;
        boolean likedYoutubeVideo = false;
        if (request.type() == AccountVideoMappingType.LIKE) {
            if (!accountCafeteriaMappingRepository.existsByTypeAndAccount_IdAndCafeteriaId(AccountCafeteriaMappingType.LIKE, account.getId(), video.getCafeteria().getId())) {
                savedCafeteriaMapping = accountCafeteriaMappingRepository.save(
                    AccountCafeteriaMapping
                        .builder(AccountCafeteriaMappingType.LIKE, account, video.getCafeteria())
                        .build()
                );
            }

            if (video.getPlatform() == VideoPlatform.YOUTUBE) {
                final String oauthMessage = messageSource.getMessage("account.oauth.not-exist", new Object[]{tokenDetail.userId(), OAuthType.GOOGLE.name()}, null);
                final AccountOAuth auth = accountOAuthRepository.findByTypeAndAccount_Id(OAuthType.GOOGLE, tokenDetail.userId())
                    .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, oauthMessage));

                final TokenResponse tokenResponse = new TokenResponse()
                    .setAccessToken(auth.getAccessToken())
                    .setRefreshToken(auth.getRefreshToken())
                    .setExpiresInSeconds(auth.getExpireSeconds())
                    .setTokenType(auth.getTokenType())
                    .setScope(auth.getScope());
                YouTube youtubeClient = googleOAuthProvider.createYoutubeClient(tokenResponse);

                try {
                    youtubeClient.videos()
                        .rate(video.getVideoPk(), "like")
                        .execute();
                    likedYoutubeVideo = true;
                } catch (IOException e) {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
                }
            }

        }

        return AccountVideoMappingCreateResponse
            .builder(savedVideoMapping)
            .cafeteriaMapping(savedCafeteriaMapping)
            .youtubeLikeSuccess(likedYoutubeVideo)
            .build();
    }

    @Transactional
    public AccountVideoMappingDeleteResponse deleteInteract(AccountVideoMappingDeleteRequest request, TokenDetail tokenDetail) {
        if (!accountVideoMappingRepository.existsByTypeAndAccountIdAndVideoId(request.type(), tokenDetail.userId(), request.videoId())) {
            final String message = messageSource.getMessage("account.video.mapping.find.not-found", new Object[]{request.type(), request.videoId(), tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }

        final AccountVideoMapping saved = accountVideoMappingRepository.findByTypeAndAccountIdAndVideoId(request.type(), tokenDetail.userId(), request.videoId()).get();
        accountVideoMappingRepository.deleteById(saved.getId());

        return AccountVideoMappingDeleteResponse
            .builder(saved)
            .build();
    }
}