package com.example.runrankback.service;

import com.example.runrankback.dto.request.CourseRequestDto;
import com.example.runrankback.dto.response.CourseResponseDto;
import com.example.runrankback.entity.Course;
import com.example.runrankback.entity.User;
import com.example.runrankback.repository.CourseRepository;
import com.example.runrankback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createCourse(CourseRequestDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("인증된 사용자를 찾을 수 없습니다: " + email));

        Course course = Course.builder()
                .user(user)
                .name(dto.getName())
                .distance(dto.getDistance())
                .encodedPolyline(dto.getEncodedPolyline())
                .startLat(dto.getStartLat())
                .startLng(dto.getStartLng())
                .visibility(dto.getVisibility()) // PUBLIC, PRIVATE
                .route(dto.getRoute()) // JSONB 백업 데이터
                .build();

        return courseRepository.save(course).getId();
    }

    // 특정 범위 내의 코스 검색
    @Transactional(readOnly = true)
    public List<CourseResponseDto> getCoursesNearby(
            Double lat, Double lng, Double range
    ) {
        Double delta = range * 0.01;    // (0.01 = 약 1.1km)

        List<Course> courses = courseRepository.findByStartLatBetweenAndStartLngBetween(
                lat - delta, lat + delta, lng - delta, lng + delta
        );

        return courses.stream()
                .map(course -> CourseResponseDto.builder()
                        .id(course.getId())
                        .name(course.getName())
                        .distance(course.getDistance())
                        .encodedPolyline(course.getEncodedPolyline())
                        .startLat(course.getStartLat())
                        .startLng(course.getStartLng())
                        .route(course.getRoute())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseResponseDto getCourseDetail(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("해당 코스가 존재하지 않습니다."));

        return CourseResponseDto.builder()
                .id(course.getId())
                .name(course.getName())
                .distance(course.getDistance())
                .encodedPolyline(course.getEncodedPolyline())
                .startLat(course.getStartLat())
                .startLng(course.getStartLng())
                .route(course.getRoute())
                .build();
    }
}