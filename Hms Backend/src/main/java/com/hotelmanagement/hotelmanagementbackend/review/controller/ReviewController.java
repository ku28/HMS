package com.hotelmanagement.hotelmanagementbackend.review.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewRequestDto;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewResponseDto;
import com.hotelmanagement.hotelmanagementbackend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Review", description = "Review Management APIs")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/api/review/post")
    @Operation(summary = "Create a review")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> createReview(
            @Valid @RequestBody ReviewRequestDto dto) {
        ReviewResponseDto response = reviewService.createReview(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("POSTSUCCESS", "Review added successfully", response));
    }

    @GetMapping("/api/review/{review_id}")
    @Operation(summary = "Get review by ID")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> getReviewById(
            @PathVariable("review_id") Integer reviewId) {
        ReviewResponseDto response = reviewService.getReviewById(reviewId);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Review retrieved", response));
    }

    @GetMapping("/api/review/all")
    @Operation(summary = "Get all reviews")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponseDto>>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<ReviewResponseDto> response = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Reviews retrieved", response));
    }

    @GetMapping("/api/reviews/rating/{rating}")
    @Operation(summary = "Get reviews by rating")
    public ResponseEntity<ApiResponse<PagedResponse<ReviewResponseDto>>> getReviewsByRating(
            @PathVariable Integer rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<ReviewResponseDto> response = reviewService.getReviewsByRating(rating, pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Reviews retrieved", response));
    }

    @GetMapping("/api/reviews/recent")
    @Operation(summary = "Get most recent review")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> getMostRecentReview() {
        ReviewResponseDto response = reviewService.getMostRecentReview();
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Review retrieved", response));
    }

    @PutMapping("/api/review/update/{review_id}")
    @Operation(summary = "Update review")
    public ResponseEntity<ApiResponse<ReviewResponseDto>> updateReview(
            @PathVariable("review_id") Integer reviewId,
            @Valid @RequestBody ReviewRequestDto dto) {
        ReviewResponseDto response = reviewService.updateReview(reviewId, dto);
        return ResponseEntity.ok(ApiResponse.success("UPDATESUCCESS", "Review updated successfully", response));
    }

    @DeleteMapping("/api/review/delete/{review_id}")
    @Operation(summary = "Delete a review")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable("review_id") Integer reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok(ApiResponse.success("DELETESUCCESS", "Review deleted successfully", null));
    }
}
