package com.hotelmanagement.hotelmanagementbackend.review.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponseMapper;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.mapper.ReviewMapper;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.reservation.repository.ReservationRepository;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewRequestDto;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewResponseDto;
import com.hotelmanagement.hotelmanagementbackend.review.entity.Review;
import com.hotelmanagement.hotelmanagementbackend.review.repository.ReviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             ReservationRepository reservationRepository,
                             ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    public ReviewResponseDto createReview(ReviewRequestDto dto) {
        if (reviewRepository.existsByReservation_ReservationId(dto.getReservationId())) {
            throw new ResourceAlreadyExistsException("Review", "reservationId", dto.getReservationId());
        }
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "reservationId", dto.getReservationId()));
        Review review = reviewMapper.toEntity(dto);
        review.setReservation(reservation);
        Review saved = reviewRepository.save(review);
        return reviewMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewById(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "reviewId", reviewId));
        return reviewMapper.toResponseDto(review);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponseDto> getAllReviews(Pageable pageable) {
        Page<Review> page = reviewRepository.findByRating(null, pageable);
        // Get all reviews using a broad rating search
        page = reviewRepository.findByReservation_ReservationId(null, pageable);
        List<ReviewResponseDto> dtos = page.getContent().stream()
                .map(reviewMapper::toResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ReviewResponseDto> getReviewsByRating(Integer rating, Pageable pageable) {
        Page<Review> page = reviewRepository.findByRating(rating, pageable);
        List<ReviewResponseDto> dtos = page.getContent().stream()
                .map(reviewMapper::toResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDto getMostRecentReview() {
        Review review = reviewRepository.findFirstByOrderByReviewDateDesc()
                .orElseThrow(() -> new ResourceNotFoundException("Review", "recent", "none"));
        return reviewMapper.toResponseDto(review);
    }

    @Override
    public ReviewResponseDto updateReview(Integer reviewId, ReviewRequestDto dto) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review", "reviewId", reviewId));
        reviewMapper.updateEntity(review, dto);
        Review updated = reviewRepository.save(review);
        return reviewMapper.toResponseDto(updated);
    }

    @Override
    public void deleteReview(Integer reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("Review", "reviewId", reviewId);
        }
        reviewRepository.deleteById(reviewId);
    }
}
