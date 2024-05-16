package com.taste.zip.tastezip.service;

import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.auth.TokenProvider;
import com.taste.zip.tastezip.auth.TokenProvider.Type;
import com.taste.zip.tastezip.auth.annotation.AccessTokenResolver;
import com.taste.zip.tastezip.dto.AuthRegistrationRequest;
import com.taste.zip.tastezip.dto.AuthRegistrationResponse;
import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.entity.AccountConfig;
import com.taste.zip.tastezip.entity.AccountOAuth;
import com.taste.zip.tastezip.entity.AdminConfig;
import com.taste.zip.tastezip.entity.enumeration.AccountConfigType;
import com.taste.zip.tastezip.entity.enumeration.AdminConfigType;
import com.taste.zip.tastezip.repository.AccountConfigRepository;
import com.taste.zip.tastezip.repository.AccountOAuthRepository;
import com.taste.zip.tastezip.repository.AccountRepository;
import com.taste.zip.tastezip.repository.AdminConfigRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final AccountOAuthRepository accountOAuthRepository;
    private final AccountConfigRepository accountConfigRepository;
    private final AdminConfigRepository adminConfigRepository;
    private final TokenProvider tokenProvider;
    private final MessageSource messageSource;



    /**
     * Registers new account for service
     * @exception Throw HttpClientErrorException(409 error) when (request.oauth.type, request.oauth.oauthPk) have to be unique
     * @exception Throw HttpClientErrorException(409 error) when no data of AccessTokenDuration or RefreshTokenDuration in AdminConfigRepository
     */
    @Transactional
    public AuthRegistrationResponse register(AuthRegistrationRequest request) {
        if (accountOAuthRepository.existsByTypeAndOauthPk(request.oauth().type(), request.oauth().oauthPk())) {
            throw new HttpClientErrorException(HttpStatus.CONFLICT, messageSource.getMessage("account.register.duplicated-oauth-pk", null, null));
        }

        final Optional<AdminConfig> accessTokenConfig = adminConfigRepository.findByType(AdminConfigType.ACCESS_TOKEN_DURATION);
        final Optional<AdminConfig> refreshTokenConfig = adminConfigRepository.findByType(AdminConfigType.REFRESH_TOKEN_DURATION);
        if (accessTokenConfig.isEmpty() || refreshTokenConfig.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.CONFLICT, messageSource.getMessage("admin.config.no-token-duration", null, null));
        }

        final Account newAccount = Account.builder(request.nickname(), request.type())
            .bio(request.bio())
            .profileImage(request.profileImage())
            .build();

        final Account savedAccount = accountRepository.save(newAccount);

        accountOAuthRepository.save(AccountOAuth.builder(savedAccount, request.oauth().type(), request.oauth().oauthPk())
            .accessToken(request.oauth().accessToken())
            .refreshToken(request.oauth().refreshToken())
            .email(request.oauth().email())
            .profileImage(request.oauth().profileImage())
            .rawData(request.oauth().rawData())
            .build());

        accountConfigRepository.saveAll(
            List.of(
                AccountConfig.builder(savedAccount)
                    .type(AccountConfigType.TERM_OF_USE_AGREEMENT)
                    .value(String.valueOf(request.config().TERM_OF_USE_AGREEMENT()))
                    .build(),
                AccountConfig.builder(savedAccount)
                    .type(AccountConfigType.TERM_OF_GPS_AGREEMENT)
                    .value(String.valueOf(request.config().TERM_OF_GPS_AGREEMENT()))
                    .build(),
                AccountConfig.builder(savedAccount)
                    .type(AccountConfigType.MARKETING_MESSAGE_AGREEMENT)
                    .value(String.valueOf(request.config().MARKETING_MESSAGE_AGREEMENT()))
                    .build()
            )
        );

        final TokenDetail tokenDetail = TokenDetail.builder(savedAccount.getId()).build();
        final String accessToken = tokenProvider.createToken(
            Duration.ofMinutes(Long.parseLong(accessTokenConfig.get().getValue())),
            Type.ACCESS_TOKEN,
            tokenDetail
        );
        final String refreshToken = tokenProvider.createToken(
            Duration.ofMinutes(Long.parseLong(refreshTokenConfig.get().getValue())),
            Type.REFRESH_TOKEN,
            tokenDetail
        );

        return new AuthRegistrationResponse(tokenDetail, accessToken, refreshToken);
    }

    /**
     * Returns parsed information of Jwt token according to JWT specification
     * @see https://datatracker.ietf.org/doc/html/rfc7519
     * @exception Throw HttpClientErrorException(401 error) when Authorization header is empty or not start with 'Bearer'
     * @exception Throw HttpClientErrorException(400 error) when given token is weired
     */
    /**
     * TODO error message들을 message properties로 이동하기
     */
    public String parseToken(HttpServletRequest request) {
        String accessHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (accessHeader == null || !accessHeader.startsWith(AccessTokenResolver.BEARER)) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "로그인 해라");
        }

        String token = accessHeader.substring(AccessTokenResolver.BEARER.length());
        String parsedToken;
        try {
            parsedToken = tokenProvider.parseToken(token);
        } catch (ExpiredJwtException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST,"토큰이 만료되었습니다. 토큰 재발급 API 호출이 필요합니다.");
        } catch (MalformedJwtException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "토큰이 변조되었습니다.");
        } catch (SignatureException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "토큰의 서명이 이상합니다.");
        } catch (IllegalArgumentException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "토큰이 null이거나 이상한 값입니다.");
        }

        return parsedToken;
    }
}
