package com.example.runrankback.controller;

import com.example.runrankback.dto.response.RankingResponseDto;
import com.example.runrankback.service.RankingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Ranking API", description = "코스 기록 랭킹 API")
@RestController
@RequestMapping("/api/rankings")
@RequiredArgsConstructor
public class RankingController {
    private final RankingService rankingService;

    @Operation(summary = "특정 코스 러닝 랭킹 조회", description = "사용자별 최상위 기록을 가져옵니다.")
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<RankingResponseDto> getCourseRankings(@PathVariable Long courseId) {
        return ResponseEntity.ok(rankingService.getCourseRankings(courseId));
    }
}