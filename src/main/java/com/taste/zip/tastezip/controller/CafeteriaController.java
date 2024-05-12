package com.taste.zip.tastezip.controller;

import com.taste.zip.tastezip.dto.CafeteriaResponse;
import com.taste.zip.tastezip.service.CafeteriaService;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CafeteriaController {

    private final CafeteriaService cafeteriaService;

    @GetMapping("/cafeteria/list")
    public ResponseEntity<Page<CafeteriaResponse>> findCafeteriaByKeyword(@RequestParam(value = "keyword") String keyword,
                                                                          @RequestParam @Positive int page,
                                                                          @RequestParam @Positive int size) {
        Page<CafeteriaResponse> responses = cafeteriaService.findByKeyword(keyword, page, size);
        return ResponseEntity.ok(responses);
    }

}
