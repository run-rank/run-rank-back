package com.example.runrankback.controller;

import com.example.runrankback.dto.response.CourseResponseDto;
import com.example.runrankback.security.CustomUserDetails;
import com.example.runrankback.service.CourseBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CourseBookmarkController {

    private final CourseBookmarkService bookmarkService;

    @PostMapping("/api/courses/{courseId}/bookmarks")
    public ResponseEntity<Void> addBookmark(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long courseId
    ) {
        bookmarkService.addBookmark(userDetails.getUser(), courseId);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/api/courses/{courseId}/bookmarks")
    public ResponseEntity<Void> removeBookmark(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long courseId
    ) {
        bookmarkService.removeBookmark(userDetails.getUser(), courseId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/bookmarks/me")
    public ResponseEntity<List<CourseResponseDto>> getMyBookmarks(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        List<CourseResponseDto> responses = bookmarkService.getMyBookmarks(userDetails.getUser());

        return ResponseEntity.ok(responses);
    }


}
