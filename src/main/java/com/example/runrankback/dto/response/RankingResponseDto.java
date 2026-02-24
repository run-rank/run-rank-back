package com.example.runrankback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class RankingResponseDto {
    private Long courseId;
    private Integer totalParticipants;
    private List<UserRankingDto> rankings;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UserRankingDto {
        private Integer rank;
        private Long userId;
        private String nickname;
        private String profileImageUrl;
        private Integer bestDuration;
    }
}