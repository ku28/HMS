package com.hotelmanagement.hotelmanagementbackend.review.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewRequestDto;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewResponseDto;
import com.hotelmanagement.hotelmanagementbackend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review-management/reviews")
@Tag(name = "Review", description = "Review Management APIs")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @Operation(summary = "Create a review")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(
            @Valid @RequestBody ReviewRequestDto dto) {
        ReviewResponseDto response = reviewService.createReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("POSTSUCCESS", "Review added successfully", response));
    }

    @PutMapping("/{review_id}")
    @Operation(summary = "Update review")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> updateReview(
            @PathVariable("review_id") Integer reviewId,
            @Valid @RequestBody ReviewRequestDto dto) {
        ReviewResponseDto response = reviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok(ApiResponse.success("UPDATESUCCESS", "Review updated successfully", response));
    }

    @DeleteMapping("/{review_id}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable("review_id") Integer reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("DELETESUCCESS", "Review deleted successfully", null));
    }
}
