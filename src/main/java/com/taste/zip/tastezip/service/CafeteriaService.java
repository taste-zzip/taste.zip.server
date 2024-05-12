package com.taste.zip.tastezip.service;

import com.taste.zip.tastezip.dto.CafeteriaResponse;
import com.taste.zip.tastezip.repository.CafeteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeteriaService {

    private final CafeteriaRepository cafeteriaRepository;

    public Page<CafeteriaResponse> findByKeyword(String keyword, Pageable pageable) {
        if (keyword.isBlank()) { // 검색어가 없을 경우
            return Page.empty();
        }

        return cafeteriaRepository.findByKeyword(keyword, pageable).map(CafeteriaResponse::from);
    }

}
