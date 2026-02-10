package com.example.runrankback.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
@Builder
public class CourseResponseDto {
    private Long id;
    private String name;
    private Integer distance;
    private String encodedPolyline;
    private Double startLat;
    private Double startLng;
    private List<Map<String, Object>> route;
}
