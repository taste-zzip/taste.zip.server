package com.taste.zip.tastezip.controller;

import com.taste.zip.tastezip.dto.CafeteriaResponse;
import com.taste.zip.tastezip.service.CafeteriaService;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    public ResponseEntity<List<CafeteriaResponse>> findCafeteriaByKeyword(@RequestParam(value = "keyword") String keyword) {
        List<CafeteriaResponse> responses = cafeteriaService.findByKeyword(keyword);
        return ResponseEntity.ok(responses);
    }

}
