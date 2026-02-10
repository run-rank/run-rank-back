package com.example.runrankback.repository;

import com.example.runrankback.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByStartLatBetweenAndStartLngBetween(
            Double minLat, Double maxLat, Double minLng, Double maxLng
    );
}
