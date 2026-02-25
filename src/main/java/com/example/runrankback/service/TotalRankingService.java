package com.example.runrankback.service;

import com.example.runrankback.dto.response.TotalRankingResponseDto;
import com.example.runrankback.entity.User;
import com.example.runrankback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class TotalRankingService {
    private final UserRepository userRepository;

    public List<TotalRankingResponseDto> getTotalRankings() {
        List<User> users = userRepository.findAllByOrderByTotalScoreDesc();

        return users.stream()
                .map(TotalRankingResponseDto::from)
                .collect(Collectors.toList());
    }
}
