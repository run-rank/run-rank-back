package com.example.runrankback.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Course extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String encodedPath;

    @Column(nullable = false, columnDefinition = "geometry(LineString, 4326)")
    private org.locationtech.jts.geom.LineString path;

    @Column(nullable = false)
    private Double distance;

    private Long savedCount = 0L;

    @Column(nullable = false, columnDefinition = "geometry(Point, 4326)")
    private org.locationtech.jts.geom.Point startPoint;

    @Builder
    public Course(User user, String title, String description, String encodedPath,
                  org.locationtech.jts.geom.LineString path, org.locationtech.jts.geom.Point startPoint,
                  Double distance) {
        this.user = user;
        this.title = title;
        this.description = description;
        this.encodedPath = encodedPath;
        this.path = path;
        this.startPoint = startPoint;
        this.distance = distance;
        this.savedCount = 0L;
    }
}