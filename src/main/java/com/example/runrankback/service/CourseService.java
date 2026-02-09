package com.example.runrankback.service;

import com.example.runrankback.dto.request.CourseRequestDto;
import com.example.runrankback.entity.Course;
import com.example.runrankback.entity.User;
import com.example.runrankback.repository.CourseRepository;
import com.example.runrankback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

//        LineString path = GeometryUtil.decodePolyline(dto.getEncodedPolyline());

//        Point startPoint = GeometryUtil.createPoint(dto.getStartLat(), dto.getStartLng());

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
}