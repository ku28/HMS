package com.hotelmanagement.hotelmanagementbackend.review.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewRequestDto;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewResponseDto;
import org.springframework.data.domain.Pageable;

public interface ReviewService {

    ReviewResponseDto createReview(ReviewRequestDto dto);
    ReviewResponseDto getReviewById(Integer reviewId);
    PagedResponse<ReviewResponseDto> getAllReviews(Pageable pageable);
    PagedResponse<ReviewResponseDto> getReviewsByRating(Integer rating, Pageable pageable);
    ReviewResponseDto getMostRecentReview();
    ReviewResponseDto updateReview(Integer reviewId, ReviewRequestDto dto);
    void deleteReview(Integer reviewId);
}
