package com.taste.zip.tastezip.controller;

import com.taste.zip.tastezip.dto.CafeteriaResponse;
import com.taste.zip.tastezip.service.CafeteriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cafeteria")
@RequiredArgsConstructor
public class CafeteriaController {

    private final CafeteriaService cafeteriaService;

    @GetMapping("/list")
    public ResponseEntity<Page<CafeteriaResponse>> findCafeteriaByKeyword(@RequestParam(value = "keyword") String keyword, Pageable pageable) {
        Page<CafeteriaResponse> responses = cafeteriaService.findByKeyword(keyword, pageable);
        return ResponseEntity.ok(responses);
    }

}
