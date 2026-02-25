package com.example.runrankback.dto.response;

import com.example.runrankback.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TotalRankingResponseDto {

    private final String userName;
    private final String profileImageUrl;
    private final long totalDistance;
    private final double totalScore;

    public static TotalRankingResponseDto from(User user) {
        return new TotalRankingResponseDto(
                user.getUserName(),
                user.getProfileImageUrl(),
                user.getTotalDistance(),
                user.getTotalScore()
        );
    }
}
