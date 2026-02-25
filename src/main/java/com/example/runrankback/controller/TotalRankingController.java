package com.example.runrankback.controller;

import com.example.runrankback.dto.response.TotalRankingResponseDto;
import com.example.runrankback.service.TotalRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rankings")
public class TotalRankingController {

    private final TotalRankingService rankingService;

    @GetMapping("/total")
    public ResponseEntity<List<TotalRankingResponseDto>> getTotalRankings() {
        List<TotalRankingResponseDto> rankings = rankingService.getTotalRankings();
        return ResponseEntity.ok(rankings);
    }
}