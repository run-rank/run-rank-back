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
    private String description;
    private Integer distance;
    private String encodedPolyline;
    private String thumbnailUrl;
    private String creatorName;
    private String creatorProfileImage;

    private Integer myBestDuration;

    public static CourseResponseDto from(Course course, Integer myBestDuration) {
        return CourseResponseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .distance(course.getDistance())
                .encodedPolyline(course.getEncodedPolyline())
                .thumbnailUrl(course.getThumbnailUrl())
                .creatorName(course.getUser().getUserName())
                .creatorProfileImage(course.getUser().getProfileImageUrl())
                .myBestDuration(myBestDuration)
                .build();
    }

    public static CourseResponseDto from(Course course) {
        return from(course, null);
    }
}
