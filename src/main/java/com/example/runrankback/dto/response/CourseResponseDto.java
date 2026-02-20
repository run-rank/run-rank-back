package com.example.runrankback.dto.response;

import com.example.runrankback.entity.Course;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CourseResponseDto {
    private Long id;
    private String name;
    private Integer distance;
    private String encodedPolyline;

    public static CourseResponseDto from(Course course) {
        return CourseResponseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .encodedPolyline(course.getEncodedPolyline())
                .build();
    }
}
