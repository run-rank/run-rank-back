package com.example.runrankback.service;

import com.example.runrankback.dto.response.RankingResponseDto;
import com.example.runrankback.repository.RunningRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {
    private final RunningRecordRepository recordRepository;

    public RankingResponseDto getCourseRankings(Long courseId) {
        List<Object[]> results = recordRepository.findBestRecordsByCourse(courseId);

        List<RankingResponseDto.UserRankingDto> rankings = new ArrayList<>();
        int currentRank = 1;

        for (Object[] row : results) {
            rankings.add(RankingResponseDto.UserRankingDto.builder()
                    .rank(currentRank++)
                    .userId(((Number) row[0]).longValue())
                    .nickname((String) row[1])
                    .bestDuration(((Number) row[2]).intValue())
                    .build());
        }

        return RankingResponseDto.builder()
                .courseId(courseId)
                .totalParticipants(rankings.size())
                .rankings(rankings)
                .build();
    }
}