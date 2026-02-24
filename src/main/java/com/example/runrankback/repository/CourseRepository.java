package com.example.runrankback.repository;

import com.example.runrankback.entity.Course;
import com.example.runrankback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findAllByOrderByCreatedAtDesc();

    @Query(value = "SELECT * FROM course c " +
            "WHERE ST_DWithin(c.path::geography, ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography, :radius) " +
            "ORDER BY c.path::geography <-> ST_SetSRID(ST_MakePoint(:lng, :lat), 4326)::geography " +
            "LIMIT 10", nativeQuery = true)
    List<Course> findNearbyCourses(@Param("lat") double lat,
                                   @Param("lng") double lng,
                                   @Param("radius") double radius);

    @Modifying
    @Query("UPDATE Course c SET c.savedCount = c.savedCount + 1 WHERE c.id = :id")
    void increaseSavedCount(@Param("id") Long id);

    @Modifying
    @Query("UPDATE Course c SET c.savedCount = c.savedCount - 1 WHERE c.id = :id")
    void decreaseSavedCount(@Param("id") Long id);

    List<Course> findAllByOrderBySavedCountDesc();

    List<Course> findAllByUserOrderByCreatedAtDesc(User user);
}
