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
import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.entity.AccountCafeteriaMapping;
import com.taste.zip.tastezip.entity.AccountOAuth;
import com.taste.zip.tastezip.entity.Cafeteria;
import com.taste.zip.tastezip.entity.Video;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import com.taste.zip.tastezip.entity.enumeration.VideoPlatform;
import com.taste.zip.tastezip.repository.AccountCafeteriaMappingRepository;
import com.taste.zip.tastezip.repository.AccountOAuthRepository;
import com.taste.zip.tastezip.repository.AccountRepository;
import com.taste.zip.tastezip.repository.CafeteriaRepository;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityNotFoundException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
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
            .orElseThrow(()-> new HttpClientErrorException(HttpStatus.NOT_FOUND, "Cafeteria not found with id " + id));

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

            videoResponses.add(VideoResponse.from(video, snippet, statistics));
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
    public AccountCafeteriaMappingDeleteResponse deleteInteract(AccountCafeteriaMappingDeleteRequest request, TokenDetail tokenDetail) {
        if (!accountCafeteriaMappingRepository.existsByTypeAndAccount_IdAndCafeteriaId(request.type(), tokenDetail.userId(), request.cafeteriaId())) {
            final String message = messageSource.getMessage("account.cafeteria.mapping.find.not-found",
                new Object[]{request.type(), request.cafeteriaId(), tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }

        final Optional<AccountCafeteriaMapping> saved = accountCafeteriaMappingRepository.findByTypeAndAccount_IdAndCafeteriaId(request.type(), tokenDetail.userId(), request.cafeteriaId());
        accountCafeteriaMappingRepository.deleteById(saved.get().getId());

        return AccountCafeteriaMappingDeleteResponse
            .builder(saved.get())
            .build();
    }
}
