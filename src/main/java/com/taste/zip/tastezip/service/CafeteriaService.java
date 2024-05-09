package com.taste.zip.tastezip.service;

import com.taste.zip.tastezip.dto.CafeteriaResponse;
import com.taste.zip.tastezip.repository.CafeteriaRepository;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeteriaService {

    private final CafeteriaRepository cafeteriaRepository;

    public List<CafeteriaResponse> findByKeyword(String keyword) {
        if (keyword.isBlank()) { // 검색어가 없을 경우
            return Collections.emptyList();
        }

        return cafeteriaRepository.findByNameContainingAndTypeContaining(keyword)
            .stream()
            .map(CafeteriaResponse::from)
            .toList();
    }

}
