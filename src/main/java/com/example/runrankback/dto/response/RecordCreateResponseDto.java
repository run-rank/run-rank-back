package com.example.runrankback.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RecordCreateResponseDto {
    private Long recordId;
    private JellyResponseDto earnedJelly;
}
