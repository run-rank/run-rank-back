package com.example.runrankback.dto.request;

import com.example.runrankback.entity.Course.Visibility;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class CourseRequestDto {

    @Schema(description = "코스 이름", example = "한강 코스")
    private String name;

    @Schema(description = "거리 (미터 단위)", example = "5200")
    private Integer distance;

    @Schema(description = "인코딩된 폴리라인 문자열", example = "polyline_string")
    private String encodedPolyline;

    @Schema(description = "전체 경로 좌표 리스트")
    private List<Map<String, Object>> route;

    @Schema(description = "공개 여부", example = "PUBLIC / PRIVATE")
    private Visibility visibility;

    @Builder
    public CourseRequestDto(String name, Integer distance, String encodedPolyline,
                            Double startLat, Double startLng,
                            List<Map<String, Object>> route, Visibility visibility) {
        this.name = name;
        this.distance = distance;
        this.encodedPolyline = encodedPolyline;
        this.route = route;
        this.visibility = visibility;
    }
}
