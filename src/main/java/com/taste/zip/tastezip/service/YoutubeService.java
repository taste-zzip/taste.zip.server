package com.taste.zip.tastezip.service;

import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.VideoListResponse;
import com.taste.zip.tastezip.auth.GoogleOAuthProvider;
import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.dto.VideoFeedResponse;
import com.taste.zip.tastezip.dto.YoutubeLikeCafeteriaUpdateRequest;
import com.taste.zip.tastezip.dto.YoutubeLikeCafeteriaUpdateResponse;
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
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class YoutubeService {

    private final AccountRepository accountRepository;
    private final AccountOAuthRepository accountOAuthRepository;
    private final VideoRepository videoRepository;
    private final AccountCafeteriaMappingRepository accountCafeteriaMappingRepository;
    private final AccountVideoMappingRepository accountVideoMappingRepository;
    private final MessageSource messageSource;
    private final GoogleOAuthProvider googleOAuthProvider;

    @Transactional
    public YoutubeLikeCafeteriaUpdateResponse updateYoutubeLike(YoutubeLikeCafeteriaUpdateRequest request, TokenDetail tokenDetail) {
        final String oauthMessage = messageSource.getMessage("account.oauth.not-exist", new Object[]{tokenDetail.userId(), OAuthType.GOOGLE.name()}, null);
        final String accountMessage = messageSource.getMessage("account.find.not-exist", new Object[]{tokenDetail.userId()}, null);

        final Account account = accountRepository.findById(tokenDetail.userId())
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, accountMessage));
        final AccountOAuth auth = accountOAuthRepository.findByTypeAndAccount_Id(OAuthType.GOOGLE, tokenDetail.userId())
            .orElseThrow(() -> new HttpClientErrorException(HttpStatus.NOT_FOUND, oauthMessage));

        final TokenResponse tokenResponse = new TokenResponse()
            .setAccessToken(auth.getAccessToken())
            .setRefreshToken(auth.getRefreshToken())
            .setExpiresInSeconds(auth.getExpireSeconds())
            .setTokenType(auth.getTokenType())
            .setScope(auth.getScope());
        YouTube youtubeClient = googleOAuthProvider.createYoutubeClient(tokenResponse);

        VideoListResponse videoListResponse = null;
        try {
            videoListResponse = youtubeClient.videos()
                .list("id, snippet")
                .setMyRating("like")
                .setMaxResults(request.size())
                .execute();
        } catch (IOException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
        }

        List<Cafeteria> likedCafeteria = new ArrayList<>();
        List<Video> likedVideo = new ArrayList<>();
        for (com.google.api.services.youtube.model.Video video : videoListResponse.getItems()) {
            final Optional<Video> serviceVideo = videoRepository.findByPlatformAndVideoPk(VideoPlatform.YOUTUBE,
                video.getId());

            if (serviceVideo.isPresent()) {
                final Cafeteria cafeteria = serviceVideo.get().getCafeteria();
                final Optional<AccountCafeteriaMapping> cafeteriaLike = accountCafeteriaMappingRepository.findByTypeAndAccount_IdAndCafeteriaId(
                    AccountCafeteriaMappingType.LIKE, tokenDetail.userId(), cafeteria.getId());
                final Optional<AccountVideoMapping> videoLike = accountVideoMappingRepository.findByTypeAndAccountIdAndVideoId(
                    AccountVideoMappingType.LIKE, tokenDetail.userId(), serviceVideo.get().getId());

                if (cafeteriaLike.isEmpty()) {
                    likedCafeteria.add(cafeteria);
                }

                if (videoLike.isEmpty()) {
                    likedVideo.add(serviceVideo.get());
                }
            }
        }
        // @see distinct() with consideration
        likedCafeteria = likedCafeteria.stream().distinct().toList();
        likedVideo = likedVideo.stream().distinct().toList();

        return new YoutubeLikeCafeteriaUpdateResponse(likedCafeteria, likedVideo);
    }
}
