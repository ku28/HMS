package com.hotelmanagement.hotelmanagementbackend.review.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.mapper.ReviewMapper;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.reservation.repository.ReservationRepository;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewRequestDto;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewResponseDto;
import com.hotelmanagement.hotelmanagementbackend.review.entity.Review;
import com.hotelmanagement.hotelmanagementbackend.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReviewService Unit Tests")
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private ReviewMapper reviewMapper;
    @InjectMocks private ReviewServiceImpl reviewService;

    private Review testReview;
    private ReviewRequestDto testRequestDto;
    private ReviewResponseDto testResponseDto;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testReservation = Reservation.builder().reservationId(1).guestName("John Doe").build();
        testReview = Review.builder().reviewId(1).reservation(testReservation)
                .rating(5).comment("Excellent stay!").reviewDate(LocalDate.now()).build();
        testRequestDto = ReviewRequestDto.builder().reservationId(1)
                .rating(5).comment("Excellent stay!").reviewDate(LocalDate.now()).build();
        testResponseDto = ReviewResponseDto.builder().reviewId(1).reservationId(1)
                .rating(5).comment("Excellent stay!").reviewDate(LocalDate.now()).build();
    }

    @Test @DisplayName("shouldCreateReviewSuccessfully")
    void shouldCreateReviewSuccessfully() {
        when(reviewRepository.existsByReservation_ReservationId(1)).thenReturn(false);
        when(reservationRepository.findById(1)).thenReturn(Optional.of(testReservation));
        when(reviewMapper.toEntity(testRequestDto)).thenReturn(testReview);
        when(reviewRepository.save(any())).thenReturn(testReview);
        when(reviewMapper.toResponseDto(testReview)).thenReturn(testResponseDto);

        ReviewResponseDto result = reviewService.createReview(testRequestDto);
        assertThat(result.getRating()).isEqualTo(5);
        assertThat(result.getComment()).isEqualTo("Excellent stay!");
    }

    @Test @DisplayName("shouldThrowExceptionWhenReviewAlreadyExistsForReservation")
    void shouldThrowExceptionWhenReviewAlreadyExistsForReservation() {
        when(reviewRepository.existsByReservation_ReservationId(1)).thenReturn(true);
        assertThatThrownBy(() -> reviewService.createReview(testRequestDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
    }

    @Test @DisplayName("shouldReturnReviewById")
    void shouldReturnReviewById() {
        when(reviewRepository.findById(1)).thenReturn(Optional.of(testReview));
        when(reviewMapper.toResponseDto(testReview)).thenReturn(testResponseDto);
        ReviewResponseDto result = reviewService.getReviewById(1);
        assertThat(result.getReviewId()).isEqualTo(1);
    }

    @Test @DisplayName("shouldThrowExceptionWhenReviewNotFound")
    void shouldThrowExceptionWhenReviewNotFound() {
        when(reviewRepository.findById(999)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.getReviewById(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("shouldReturnReviewsByRating")
    void shouldReturnReviewsByRating() {
        Pageable p = PageRequest.of(0, 10);
        Page<Review> page = new PageImpl<>(List.of(testReview), p, 1);
        when(reviewRepository.findByRating(5, p)).thenReturn(page);
        when(reviewMapper.toResponseDto(testReview)).thenReturn(testResponseDto);
        PagedResponse<ReviewResponseDto> result = reviewService.getReviewsByRating(5, p);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getRating()).isEqualTo(5);
    }

    @Test @DisplayName("shouldReturnMostRecentReview")
    void shouldReturnMostRecentReview() {
        when(reviewRepository.findFirstByOrderByReviewDateDesc()).thenReturn(Optional.of(testReview));
        when(reviewMapper.toResponseDto(testReview)).thenReturn(testResponseDto);
        ReviewResponseDto result = reviewService.getMostRecentReview();
        assertThat(result).isNotNull();
    }

    @Test @DisplayName("shouldThrowExceptionWhenNoRecentReviewExists")
    void shouldThrowExceptionWhenNoRecentReviewExists() {
        when(reviewRepository.findFirstByOrderByReviewDateDesc()).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reviewService.getMostRecentReview())
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("shouldUpdateReviewSuccessfully")
    void shouldUpdateReviewSuccessfully() {
        ReviewRequestDto updateDto = ReviewRequestDto.builder().reservationId(1)
                .rating(4).comment("Good stay").reviewDate(LocalDate.now()).build();
        ReviewResponseDto updatedResponse = ReviewResponseDto.builder().reviewId(1)
                .reservationId(1).rating(4).comment("Good stay").reviewDate(LocalDate.now()).build();

        when(reviewRepository.findById(1)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any())).thenReturn(testReview);
        when(reviewMapper.toResponseDto(testReview)).thenReturn(updatedResponse);

        ReviewResponseDto result = reviewService.updateReview(1, updateDto);
        assertThat(result.getRating()).isEqualTo(4);
        verify(reviewMapper).updateEntity(testReview, updateDto);
    }

    @Test @DisplayName("shouldDeleteReviewSuccessfully")
    void shouldDeleteReviewSuccessfully() {
        when(reviewRepository.existsById(1)).thenReturn(true);
        reviewService.deleteReview(1);
        verify(reviewRepository).deleteById(1);
    }

    @Test @DisplayName("shouldThrowExceptionWhenDeletingNonExistentReview")
    void shouldThrowExceptionWhenDeletingNonExistentReview() {
        when(reviewRepository.existsById(999)).thenReturn(false);
        assertThatThrownBy(() -> reviewService.deleteReview(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
