package com.example.runrankback.service;

import com.example.runrankback.dto.request.CourseRequestDto;
import com.example.runrankback.dto.response.CourseResponseDto;
import com.example.runrankback.entity.Course;
import com.example.runrankback.entity.User;
import com.example.runrankback.repository.CourseRepository;
import com.example.runrankback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public List<CourseResponseDto> getNearbyCourses(double lat, double lng, double radius) {
        return courseRepository.findNearbyCourses(lat, lng, radius)
                .stream()
                .map(CourseResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long createCourse(CourseRequestDto requestDto, User user) {
        LineString path = createLineStrFromRoute(requestDto.getRoute());

        Course course = Course.builder()
                .user(user)
                .name(requestDto.getName())
                .description(requestDto.getDescription())
                .distance(requestDto.getDistance())
                .encodedPolyline(requestDto.getEncodedPolyline())
                .path(path)
                .route(requestDto.getRoute())
                .visibility(Course.Visibility.valueOf(requestDto.getVisibility().name()))
                .build();

        return courseRepository.save(course).getId();
    }

    private LineString createLineStrFromRoute(List<Map<String, Object>> route) {
        if(route == null || route.isEmpty()) {
            throw new IllegalArgumentException("코스 경로 데이터가 없습니다.");
        }

        try{
            Coordinate[] coordinates = route.stream()
                    .map(pointMap -> {
                        double lat = ((Number) pointMap.get("lat")).doubleValue();
                        double lng = ((Number) pointMap.get("lng")).doubleValue();

                        return new Coordinate(lng, lat);
                    })
                    .toArray(Coordinate[]::new);

            return geometryFactory.createLinearRing(coordinates);
        }
        catch(Exception e) {
            throw new IllegalArgumentException("올바르지 않은 경로 데이터 형식: " + e.getMessage());
        }
    }
}