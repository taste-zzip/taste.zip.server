package com.taste.zip.tastezip.service;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import com.google.api.services.youtube.model.VideoSnippet;
import com.google.api.services.youtube.model.VideoStatistics;
import com.taste.zip.tastezip.auth.GoogleOAuthProvider;
import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.dto.*;
import com.taste.zip.tastezip.dto.CafeteriaCommentListResponse.CommentItem;
import com.taste.zip.tastezip.dto.CafeteriaLikeResponse.CafeteriaLike;
import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.entity.AccountCafeteriaMapping;
import com.taste.zip.tastezip.entity.AccountOAuth;
import com.taste.zip.tastezip.entity.AccountVideoMapping;
import com.taste.zip.tastezip.entity.Cafeteria;
import com.taste.zip.tastezip.entity.Comment;
import com.taste.zip.tastezip.entity.Video;
import com.taste.zip.tastezip.entity.enumeration.AccountCafeteriaMappingType;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import com.taste.zip.tastezip.entity.enumeration.VideoPlatform;
import com.taste.zip.tastezip.repository.AccountCafeteriaMappingRepository;
import com.taste.zip.tastezip.repository.AccountOAuthRepository;
import com.taste.zip.tastezip.repository.AccountRepository;
import com.taste.zip.tastezip.repository.AccountVideoMappingRepository;
import com.taste.zip.tastezip.repository.CafeteriaRepository;
import com.taste.zip.tastezip.repository.CommentRepository;
import com.taste.zip.tastezip.repository.VideoRepository;
import java.io.IOException;
import java.util.*;

import jakarta.persistence.EntityNotFoundException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeteriaService {

    private final AccountRepository accountRepository;
    private final AccountOAuthRepository accountOAuthRepository;
    private final CafeteriaRepository cafeteriaRepository;
    private final AccountCafeteriaMappingRepository accountCafeteriaMappingRepository;
    private final AccountVideoMappingRepository accountVideoMappingRepository;
    private final CommentRepository commentRepository;
    private final VideoRepository videoRepository;
    private final MessageSource messageSource;
    private final GoogleOAuthProvider googleOAuthProvider;

    public Page<CafeteriaResponse> findByKeyword(String keyword, Pageable pageable, TokenDetail tokenDetail) {
        if (keyword.isBlank()) { // 검색어가 없을 경우
            return Page.empty();
        }

        if (!accountRepository.existsById(tokenDetail.userId())) {
            final String message = messageSource.getMessage("account.find.not-exist",
                    new Object[]{tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }

        return cafeteriaRepository.findByKeyword(keyword, pageable).map(CafeteriaResponse::from);
    }

    public CafeteriaDetailResponse getById(Long id, TokenDetail tokenDetail) {
        final Cafeteria cafeteria = cafeteriaRepository.findById(id)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Cafeteria not found with id " + id));

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

        List<VideoResponse> videoResponses = new ArrayList<>();

        for (Video video : cafeteria.getVideos()) {
            // Youtube Video
            VideoListResponse videoResponse = null;
            VideoSnippet snippet = null;
            VideoStatistics statistics = null;

            if (youtubeClient != null && video.getPlatform() == VideoPlatform.YOUTUBE) {
                try {
                    videoResponse = youtubeClient.videos().list("id, snippet, statistics")
                            .setId(video.getVideoPk())
                            .execute();
                } catch (IOException e) {
                    throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
                } catch (Exception e) {
                    videoResponse = null;
                }

                if (videoResponse != null && !videoResponse.getItems().isEmpty()) {
                    final com.google.api.services.youtube.model.Video youtubeVideo = videoResponse.getItems().get(0);
                    snippet = youtubeVideo.getSnippet();
                    statistics = youtubeVideo.getStatistics();
                }
            }

            final List<AccountVideoMapping> videoMappings = accountVideoMappingRepository.findAllByAccount_IdAndVideoId(
                    tokenDetail.userId(), video.getId());

            videoResponses.add(VideoResponse.from(video, snippet, statistics, VideoResponse.AccountMapping.of(videoMappings)));
        }

        return CafeteriaDetailResponse.from(cafeteria, videoResponses);
    }

    @Transactional
    public AccountCafeteriaMappingCreateResponse saveInteract(AccountCafeteriaMappingCreateRequest request, TokenDetail tokenDetail) {
        if (!accountRepository.existsById(tokenDetail.userId())) {
            final String message = messageSource.getMessage("account.find.not-exist",
                    new Object[]{tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }
        if (!cafeteriaRepository.existsById(request.cafeteriaId())) {
            final String message = messageSource.getMessage("cafeteria.find.not-exist",
                    new Object[]{request.cafeteriaId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }
        if (accountCafeteriaMappingRepository.existsByTypeAndAccount_IdAndCafeteriaId(request.type(), tokenDetail.userId(), request.cafeteriaId())) {
            final String message = messageSource.getMessage("account.cafeteria.mapping.find.duplicated",
                    new Object[]{request.type(), request.cafeteriaId(), tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.CONFLICT, message);
        }

        final Optional<Cafeteria> cafeteria = cafeteriaRepository.findById(request.cafeteriaId());
        final Optional<Account> account = accountRepository.findById(tokenDetail.userId());

        final AccountCafeteriaMapping saved = accountCafeteriaMappingRepository.save(
                AccountCafeteriaMapping
                        .builder(request.type(), account.get(), cafeteria.get())
                        .build()
        );

        return AccountCafeteriaMappingCreateResponse
                .builder(saved)
                .build();
    }

    @Transactional
    public AccountCafeteriaMappingDeleteResponse deleteInteract(Long cafeteriaId, AccountCafeteriaMappingType type, TokenDetail tokenDetail) {
        if (!accountCafeteriaMappingRepository.existsByTypeAndAccount_IdAndCafeteriaId(type, tokenDetail.userId(), cafeteriaId)) {
            final String message = messageSource.getMessage("account.cafeteria.mapping.find.not-found",
                    new Object[]{type, cafeteriaId, tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }

        final Optional<AccountCafeteriaMapping> saved = accountCafeteriaMappingRepository.findByTypeAndAccount_IdAndCafeteriaId(type, tokenDetail.userId(), cafeteriaId);
        accountCafeteriaMappingRepository.deleteById(saved.get().getId());

        return AccountCafeteriaMappingDeleteResponse
                .builder(saved.get())
                .build();
    }

    public CafeteriaLikeResponse getCafeteriaLiked(TokenDetail tokenDetail) {
        if (!accountRepository.existsById(tokenDetail.userId())) {
            final String message = messageSource.getMessage("account.find.not-exist",
                    new Object[]{tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }

        final List<CafeteriaLikeResponse.CafeteriaLike> list = new ArrayList<>();
        final List<AccountCafeteriaMapping> likeMappings = accountCafeteriaMappingRepository.findAllByTypeAndAccount_Id(
                AccountCafeteriaMappingType.LIKE, tokenDetail.userId());
        final List<Cafeteria> cafeteriaList = likeMappings.stream().map(AccountCafeteriaMapping::getCafeteria).toList();

        for (Cafeteria cafeteria : cafeteriaList) {
            final List<Video> topVideos = videoRepository.findTopByCafeteriaId(5L, cafeteria.getId());

            List<VideoResponse> videoResponses = new ArrayList<>();
            for (Video video : topVideos) {
                VideoListResponse videoResponse = null;
                VideoSnippet snippet = null;
                VideoStatistics statistics = null;

                if (video.getPlatform() == VideoPlatform.YOUTUBE) {
                    Optional<AccountOAuth> accountGoogle = accountOAuthRepository.findByTypeAndAccount_Id(OAuthType.GOOGLE, tokenDetail.userId());
                    if (accountGoogle.isPresent()) {
                        AccountOAuth auth = accountGoogle.get();
                        TokenResponse tokenResponse = new TokenResponse()
                                .setAccessToken(auth.getAccessToken())
                                .setRefreshToken(auth.getRefreshToken())
                                .setExpiresInSeconds(auth.getExpireSeconds())
                                .setTokenType(auth.getTokenType())
                                .setScope(auth.getScope());
                        YouTube youtubeClient = googleOAuthProvider.createYoutubeClient(tokenResponse);
                        try {
                            videoResponse = youtubeClient.videos().list("id,snippet,statistics")
                                    .setId(video.getVideoPk())
                                    .execute();
                        } catch (IOException e) {
                            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
                        } catch (Exception e) {
                            videoResponse = null;
                        }

                        if (videoResponse != null && !videoResponse.getItems().isEmpty()) {
                            com.google.api.services.youtube.model.Video youtubeVideo = videoResponse.getItems().get(0);
                            snippet = youtubeVideo.getSnippet();
                            statistics = youtubeVideo.getStatistics();
                        }
                    }
                }

                List<AccountVideoMapping> videoMappings = accountVideoMappingRepository.findAllByAccount_IdAndVideoId(
                        tokenDetail.userId(), video.getId());

                videoResponses.add(VideoResponse.from(video, snippet, statistics, VideoResponse.AccountMapping.of(videoMappings)));
            }

            list.add(new CafeteriaLike(cafeteria, videoResponses));
        }

        return new CafeteriaLikeResponse(list);
    }

    public CafeteriaCommentListResponse readCafeteriaCommentList(Long cafeteriaId, Pageable pageable, TokenDetail tokenDetail) {
        final String accountMessage = messageSource.getMessage("account.find.not-exist", new Object[]{tokenDetail.userId()}, null);
        final String cafeteriaMessage = messageSource.getMessage("cafeteria.find.not-exist", new Object[]{cafeteriaId}, null);

        accountRepository.findById(tokenDetail.userId())
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, accountMessage));
        cafeteriaRepository.findById(cafeteriaId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, cafeteriaMessage));

        final Page<CommentItem> commentItems = commentRepository.findAllByCafeteriaId(cafeteriaId, pageable)
                .map(comment -> new CommentItem(comment, comment.getAccount()));

        return new CafeteriaCommentListResponse(commentItems);
    }

    @Transactional
    public CafeteriaCommentCreateResponse createComment(CafeteriaCommentCreateRequest request, Long cafeteriaId, TokenDetail tokenDetail) {
        final String accountMessage = messageSource.getMessage("account.find.not-exist", new Object[]{tokenDetail.userId()}, null);
        final String cafeteriaMessage = messageSource.getMessage("cafeteria.find.not-exist", new Object[]{cafeteriaId}, null);

        final Account account = accountRepository.findById(tokenDetail.userId())
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, accountMessage));
        final Cafeteria cafeteria = cafeteriaRepository.findById(cafeteriaId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, cafeteriaMessage));

        final Comment saved = commentRepository.save(Comment
                .builder(request.content(), account, cafeteria)
                .build());

        return new CafeteriaCommentCreateResponse(saved);
    }

    @Transactional
    public CafeteriaCommentDeleteResponse deleteComment(Long commentId, TokenDetail tokenDetail) {
        final String accountMessage = messageSource.getMessage("account.find.not-exist", new Object[]{tokenDetail.userId()}, null);
        final String commentMessage = messageSource.getMessage("comment.find.not-exist", new Object[]{commentId}, null);
        final String commentAuthMessage = messageSource.getMessage("comment.find.not-authorized", new Object[]{tokenDetail.userId(), commentId}, null);

        final Account account = accountRepository.findById(tokenDetail.userId())
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, accountMessage));
        final Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, commentMessage));

        if (!Objects.equals(comment.getAccount().getId(), account.getId())) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN, commentAuthMessage);
        }

        commentRepository.deleteById(comment.getId());

        return new CafeteriaCommentDeleteResponse(comment);
    }

    public List<CafeteriaDetailResponse> getRecommendations(TokenDetail tokenDetail) {
        long userId = tokenDetail.userId();
        List<AccountCafeteriaMapping> mappings = accountCafeteriaMappingRepository.findAllByAccount_Id(userId);

        // AccountCafeteriaMapping에서 사용자가 누른 상호작용의 type 개수 구함.
        Map<String, Long> typeCounts = mappings.stream()
                .filter(mapping -> mapping.getAccount().getId() == userId)
                .map(mapping -> mapping.getCafeteria().getType())
                .collect(Collectors.groupingBy(type -> type, Collectors.counting()));

        // 등장 횟수에 따라 type을 정렬
        List<Map.Entry<String, Long>> sortedTypes = typeCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .toList();

        List<Cafeteria> recommendedCafeterias;
        if (sortedTypes.size() >= 2) {
            String mostCommonType = sortedTypes.get(0).getKey();
            String secondMostCommonType = sortedTypes.get(1).getKey();

            // 가장 많이 등장한 type에서 3개의 Cafeteria를 검색
            List<Cafeteria> top3Cafeterias = cafeteriaRepository.findTop3ByType(mostCommonType);

            // 두 번째로 많이 등장한 type에서 2개의 Cafeteria를 검색
            List<Cafeteria> top2Cafeterias = cafeteriaRepository.findTop2ByType(secondMostCommonType);

            // 합치기
            recommendedCafeterias = Stream.concat(top3Cafeterias.stream(), top2Cafeterias.stream())
                    .collect(Collectors.toList());
        } else {
            // 좋아요 누른 type의 개수가 부족할 경우 무작위로 Cafeteria를 추출 (초기 사용자의 경우)
            recommendedCafeterias = cafeteriaRepository.findAll();
            Collections.shuffle(recommendedCafeterias);
            recommendedCafeterias = recommendedCafeterias.stream().limit(5).collect(Collectors.toList());
        }

        return recommendedCafeterias.stream()
                .map(cafeteria -> getById(cafeteria.getId(), tokenDetail))
                .collect(Collectors.toList());
    }

}
