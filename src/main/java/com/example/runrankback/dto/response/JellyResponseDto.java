package com.example.runrankback.dto.response;

import com.example.runrankback.entity.Jelly;
import com.example.runrankback.entity.JellyColor;
import com.example.runrankback.entity.JellyType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class JellyResponseDto {
    private Long id;
    private JellyType type;
    private JellyColor color;
    private String typeDescription;
    private String colorDescription;
    private boolean isNew;

    public static JellyResponseDto fromEntity(Jelly jelly) {
        return JellyResponseDto.builder()
                .id(jelly.getId())
                .type(jelly.getType())
                .color(jelly.getColor())
                .typeDescription(jelly.getType().getDescription())
                .colorDescription(jelly.getColor().getDescription())
                .build();
    }
}
