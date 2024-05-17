package com.taste.zip.tastezip.service;

import com.taste.zip.tastezip.auth.TokenDetail;
import com.taste.zip.tastezip.auth.annotation.AccessToken;
import com.taste.zip.tastezip.dto.CafeteriaResponse;
import com.taste.zip.tastezip.entity.Cafeteria;
import com.taste.zip.tastezip.repository.AccountRepository;
import com.taste.zip.tastezip.repository.CafeteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeteriaService {

    private final AccountRepository accountRepository;
    private final CafeteriaRepository cafeteriaRepository;
    private final MessageSource messageSource;

    public Page<CafeteriaResponse> findByKeyword(String keyword, Pageable pageable, TokenDetail tokenDetail) {
        if (keyword.isBlank()) { // 검색어가 없을 경우
            return Page.empty();
        }

        if (!accountRepository.existsById(tokenDetail.userId())) {
            final String message = messageSource.getMessage("account.find.not-exist",
                new Object[]{tokenDetail.userId()}, null);
            throw new HttpClientErrorException(HttpStatusCode.valueOf(404), message);
        }

        return cafeteriaRepository.findByKeyword(keyword, pageable).map(CafeteriaResponse::from);
    }

}
