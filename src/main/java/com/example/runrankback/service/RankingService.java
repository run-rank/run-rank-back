package com.example.runrankback.service;

import com.example.runrankback.dto.response.RankingResponseDto;
import com.example.runrankback.repository.RunningRecordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RankingService {
    private final RunningRecordRepository recordRepository;

    public RankingResponseDto getCourseRankings(Long courseId) {
        List<RunningRecordRepository.RankingItem> results = recordRepository.findBestRecordsByCourse(courseId);

        List<RankingResponseDto.UserRankingDto> rankings = IntStream.range(0, results.size())
                .mapToObj(i -> {
                    RunningRecordRepository.RankingItem item = results.get(i);
                    return RankingResponseDto.UserRankingDto.builder()
                            .rank(i + 1)
                            .userId(item.getUserId())
                            .nickname(item.getNickname())
                            .profileImageUrl(item.getProfileImageUrl())
                            .bestDuration(item.getBestDuration())
                            .build();
                })
                .collect(Collectors.toList());

        return RankingResponseDto.builder()
                .courseId(courseId)
                .totalParticipants(rankings.size())
                .rankings(rankings)
                .build();
    }
}