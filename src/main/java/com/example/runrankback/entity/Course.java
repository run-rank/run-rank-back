package com.example.runrankback.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "courses")
public class Course extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer distance; // 미터 단위

    @Column(columnDefinition = "TEXT", nullable = false)
    private String encodedPolyline;

    @Column(columnDefinition = "geometry(LineString, 4326)")
    private LineString path;

    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point startPoint;

    private Double startLat;
    private Double startLng;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility; // PUBLIC, PRIVATE

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> route;

    public enum Visibility {
        PUBLIC, PRIVATE
    }
}