package com.taste.zip.tastezip.service;

import com.taste.zip.tastezip.dto.CafeteriaResponse;
import com.taste.zip.tastezip.entity.Cafeteria;
import com.taste.zip.tastezip.repository.CafeteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeteriaService {

    private final CafeteriaRepository cafeteriaRepository;

    public Page<CafeteriaResponse> findByKeyword(String keyword, int page, int size) {
        if (keyword.isBlank()) { // 검색어가 없을 경우
            return Page.empty();
        }

        // 일차적으로 id -> 거리 가까운 쪽이 필요한지는 논의 후 적용
        PageRequest pageRequest = PageRequest.of(page - 1, size, Sort.by("id").descending());
        Page<Cafeteria> cafeteriaPage = cafeteriaRepository.findByKeyword(keyword, pageRequest);
        return cafeteriaPage.map(CafeteriaResponse::from);
    }

}
