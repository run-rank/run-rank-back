package com.example.runrankback.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@NoArgsConstructor
public class RecordRequestDto {

    @NotNull(message = "코스 ID는 필수입니다.")
    private Long courseId;

    @NotNull(message = "러닝 시간은 필수입니다.")
    @Min(value = 1, message = "0보다 큰 값만 가능합니다.")
    private Integer duration;

    @NotNull(message = "거리는 필수입니다.")
    @Min(value = 1, message = "0보다 큰 값만 가능합니다.")
    private Integer distance;

    @NotNull(message = "날짜는 필수입니다.")
    private LocalDate runDate;

    private List<Map<String, Object>> gpsRoute;

    @Builder
    public RecordRequestDto(
            Long courseId,
            Integer duration, Integer distance,
            LocalDate runDate, List<Map<String, Object>> gpsRoute
    ) {
        this.courseId = courseId;
        this.duration = duration;
        this.distance = distance;
        this.runDate = runDate;
        this.gpsRoute = gpsRoute;
    }
}
