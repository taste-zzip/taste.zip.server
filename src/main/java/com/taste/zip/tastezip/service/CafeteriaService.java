package com.taste.zip.tastezip.service;

import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingCreateRequest;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingCreateResponse;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingDeleteRequest;
import com.taste.zip.tastezip.dto.AccountCafeteriaMappingDeleteResponse;
import com.taste.zip.tastezip.dto.CafeteriaResponse;
import com.taste.zip.tastezip.entity.Account;
import com.taste.zip.tastezip.entity.AccountCafeteriaMapping;
import com.taste.zip.tastezip.entity.Cafeteria;
import com.taste.zip.tastezip.repository.AccountCafeteriaMappingRepository;
import com.taste.zip.tastezip.repository.AccountRepository;
import com.taste.zip.tastezip.repository.CafeteriaRepository;
import java.util.Optional;
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
    private final CafeteriaRepository cafeteriaRepository;
    private final AccountCafeteriaMappingRepository accountCafeteriaMappingRepository;
    private final MessageSource messageSource;

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
