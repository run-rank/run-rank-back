package com.example.runrankback.dto.response;

import com.example.runrankback.entity.RunningRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class RecordResponseDto {
    private Long id;
    private Integer duration;
    private Integer distance;
    private LocalDate runDate;
    private List<Map<String, Object>> gpsRoute;

    public static RecordResponseDto fromEntity(RunningRecord record) {
        return RecordResponseDto.builder()
                .id(record.getId())
                .duration(record.getDuration())
                .distance(record.getDistance())
                .runDate(record.getRunDate())
                .gpsRoute(record.getGpsRoute())
                .build();
    }
}