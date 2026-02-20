package com.example.runrankback.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import org.hibernate.annotations.Type;
import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.LineString;

import java.util.List;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private Integer distance; // 미터 단위

    @Column(columnDefinition = "TEXT", nullable = false)
    private String encodedPolyline;

    @Column(columnDefinition = "geometry(LineString, 4326)")
    private LineString path;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Visibility visibility; // PUBLIC, PRIVATE

    @Type(JsonBinaryType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, Object>> route;

    @Builder.Default
    private Long savedCount = 0L;

    public enum Visibility {
        PUBLIC, PRIVATE
    }

    public void incrementSavedCnt() {
        this.savedCount++;
    }
}