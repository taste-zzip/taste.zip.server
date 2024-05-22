package com.taste.zip.tastezip.service;

import com.taste.zip.tastezip.auth.OAuthCredential;
import com.taste.zip.tastezip.auth.OAuthProvider;
import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.auth.TokenProvider;
import com.taste.zip.tastezip.auth.TokenProvider.Type;
import com.taste.zip.tastezip.auth.annotation.AccessTokenResolver;
import com.taste.zip.tastezip.dto.AccountDeleteResponse;
import com.taste.zip.tastezip.dto.AccountDetailResponse;
import com.taste.zip.tastezip.dto.AccountUpdateRequest;
import com.taste.zip.tastezip.dto.AuthRegistrationRequest;
import com.taste.zip.tastezip.dto.AuthRegistrationResponse;
import com.taste.zip.tastezip.dto.AccountUpdateResponse;
import com.taste.zip.tastezip.dto.LoginRequest;
import com.taste.zip.tastezip.dto.LoginResponse;
import com.taste.zip.tastezip.dto.OAuthLoginUriResponse;
import com.taste.zip.tastezip.dto.OAuthLoginUriResponse.OAuthLoginUri;
import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.entity.AccountConfig;
import com.taste.zip.tastezip.entity.AccountOAuth;
import com.taste.zip.tastezip.entity.AdminConfig;
import com.taste.zip.tastezip.entity.enumeration.AccountConfigType;
import com.taste.zip.tastezip.entity.enumeration.AdminConfigType;
import com.taste.zip.tastezip.entity.enumeration.OAuthType;
import com.taste.zip.tastezip.repository.AccountConfigRepository;
import com.taste.zip.tastezip.repository.AccountOAuthRepository;
import com.taste.zip.tastezip.repository.AccountRepository;
import com.taste.zip.tastezip.repository.AdminConfigRepository;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
    private final List<OAuthProvider> providerList;

    private OAuthProvider findProvider(OAuthType type) {
        for (OAuthProvider provider : providerList) {
            if (provider.getType() == type) {
                return provider;
            }
        }

        return null;
    }

    public OAuthLoginUriResponse findLoginUri(@Nullable OAuthType type) {
        if (type == null) {
            return new OAuthLoginUriResponse(
                providerList.stream()
                    .map(OAuthLoginUri::of)
                    .toList()
            );
        }

        final OAuthProvider provider = findProvider(type);

        if (provider == null) {
            final String message = messageSource.getMessage("account.oauth.provider.not-found",
                new Object[]{type.name()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }

        final OAuthLoginUri loginUri = OAuthLoginUri.of(provider);

        return OAuthLoginUriResponse.builder()
            .oauth(List.of(loginUri))
            .build();
    }

    public LoginResponse login(LoginRequest request) {
        /**
         * TODO registration 함수에 중복되는 부분 처리하기
         */
        final Optional<AdminConfig> accessTokenConfig = adminConfigRepository.findByType(AdminConfigType.ACCESS_TOKEN_DURATION);
        final Optional<AdminConfig> refreshTokenConfig = adminConfigRepository.findByType(AdminConfigType.REFRESH_TOKEN_DURATION);
        if (accessTokenConfig.isEmpty() || refreshTokenConfig.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.CONFLICT, messageSource.getMessage("admin.config.no-token-duration", null, null));
        }

        final OAuthProvider provider = findProvider(request.type());

        if (provider == null) {
            final String message = messageSource.getMessage("account.oauth.provider.not-found",
                new Object[]{request.type().name()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }

        final OAuthCredential credential = provider.authorize(request.code());

        if (!accountOAuthRepository.existsByTypeAndOauthPk(request.type(), credential.user().oauthPk())) {
            final String message = messageSource.getMessage("account.find.not-exist",
                new Object[]{credential.user().oauthPk()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }

        final Optional<AccountOAuth> accountOAuth = accountOAuthRepository.findByTypeAndOauthPk(request.type(),
            credential.user().oauthPk());
        final Long accountId = accountOAuth.get().getAccount().getId();

        /**
         * TODO registration 함수에 중복되는 부분 처리하기
         */
        final TokenDetail tokenDetail = TokenDetail.builder(accountId).build();
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

        return new LoginResponse(tokenDetail, accessToken, refreshToken);
    }

    /**
     * Registers new account for service
     * @exception Throw HttpClientErrorException(409 error) when (request.oauth.type, request.oauth.oauthPk) have to be unique
     * @exception Throw HttpClientErrorException(409 error) when no data of AccessTokenDuration or RefreshTokenDuration in AdminConfigRepository
     */
    @Transactional
    public AuthRegistrationResponse register(AuthRegistrationRequest request) {
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

        final OAuthProvider provider = findProvider(request.oauth().type());
        if (provider == null) {
            final String message = messageSource.getMessage("account.oauth.provider.not-found",
                new Object[]{request.oauth().type().name()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, message);
        }

        final OAuthCredential credential = provider.authorize(request.oauth().code());
        if (accountOAuthRepository.existsByTypeAndOauthPk(request.oauth().type(), credential.user().oauthPk())) {
            throw new HttpClientErrorException(HttpStatus.CONFLICT, messageSource.getMessage("account.register.duplicated-oauth-pk", null, null));
        }

        accountOAuthRepository.save(AccountOAuth.builder(savedAccount, request.oauth().type(), credential.user().oauthPk())
            .accessToken(credential.token().accessToken())
            .refreshToken(credential.token().refreshToken())
            .expireSeconds(credential.token().expireSeconds())
            .tokenType(credential.token().tokenType())
            .scope(credential.token().scope())
            .email(credential.user().email())
            .profileImage(credential.user().profileImage())
            .rawData(credential.user().rawData())
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
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "토큰이 만료되었습니다. 토큰 재발급 API 호출이 필요합니다.");
        } catch (MalformedJwtException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "토큰이 변조되었습니다.");
        } catch (SignatureException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "토큰의 서명이 이상합니다.");
        } catch (IllegalArgumentException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "토큰이 null이거나 이상한 값입니다.");
        }

        return parsedToken;
    }

    /**
     * Returns acccount information of tokenDetial
     * @exception Throw 404 error when account of tokenDetial doesn't exist
     * @exception Throw HttpClientErrorException(401 error) when Authorization header is empty or not start with 'Bearer'
     * @exception Throw HttpClientErrorException(400 error) when given token is weired
     */
    public AccountDetailResponse findMyAccount(TokenDetail tokenDetail) {
        final Optional<Account> account = accountRepository.findById(tokenDetail.userId());

        if (account.isEmpty()) {
            final String errorMessage = messageSource.getMessage("account.find.not-exist",
                new Object[]{tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, errorMessage);
        }

        final List<AccountOAuth> accountOAuthList = accountOAuthRepository.findAllByAccount_Id(account.get().getId());
        final List<AccountConfig> accountConfigList = accountConfigRepository.findAllByAccount_Id(account.get().getId());

        return AccountDetailResponse
            .builder(
                account.get(),
                accountOAuthList,
                accountConfigList
            )
            .build();
    }

    @Transactional
    public AccountUpdateResponse updateMyAccount(AccountUpdateRequest request, TokenDetail tokenDetail) {
        final Optional<Account> account = accountRepository.findById(tokenDetail.userId());

        if (account.isEmpty()) {
            final String errorMessage = messageSource.getMessage("account.find.not-exist",
                new Object[]{tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, errorMessage);
        }

        return new AccountUpdateResponse(account.get().update(request));
    }

    /**
     * Delete user entity and other entities related
     * @exception Throw 404 error when account of tokenDetial doesn't exist
     * @exception Throw HttpClientErrorException(401 error) when Authorization header is empty or not start with 'Bearer'
     * @exception Throw HttpClientErrorException(400 error) when given token is weired
     */
    @Transactional
    public AccountDeleteResponse deleteMyAccount(TokenDetail tokenDetail) {
        final Optional<Account> account = accountRepository.findById(tokenDetail.userId());

        if (account.isEmpty()) {
            final String errorMessage = messageSource.getMessage("account.find.not-exist",
                new Object[]{tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND, errorMessage);
        }

        accountRepository.delete(account.get());

        return new AccountDeleteResponse(account.get());
    }
}
