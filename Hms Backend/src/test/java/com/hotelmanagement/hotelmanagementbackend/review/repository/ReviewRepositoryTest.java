package com.hotelmanagement.hotelmanagementbackend.review.repository;

import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import com.hotelmanagement.hotelmanagementbackend.review.entity.Review;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.reservation.repository.ReservationRepository;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryDataJpaTest
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Test
    void shouldSaveReviewSuccessfully() {

        Review review = Review.builder()
                .rating(5)
                .comment("Excellent Service")
                .reviewDate(LocalDate.now())
                .build();

        Review savedReview =
                reviewRepository.save(review);

        assertNotNull(savedReview);

        assertNotNull(savedReview.getReviewId());
    }

    @Test
    void shouldFindReviewById() {

        Review review = Review.builder()
                .rating(4)
                .comment("Very Good")
                .reviewDate(LocalDate.now())
                .build();

        Review savedReview =
                reviewRepository.save(review);

        Optional<Review> foundReview =
                reviewRepository.findById(
                        savedReview.getReviewId()
                );

        assertTrue(foundReview.isPresent());

        assertEquals(
                "Very Good",
                foundReview.get().getComment()
        );
    }

    @Test
    void shouldReturnReviewsByRating() {

        Review review = Review.builder()
                .rating(5)
                .comment("Luxury Stay")
                .reviewDate(LocalDate.now())
                .build();

        reviewRepository.save(review);

        Page<Review> reviews =
                reviewRepository.findByRating(
                        5,
                        PageRequest.of(0, 5)
                );

        assertFalse(reviews.isEmpty());

        assertEquals(
                5,
                reviews.getContent().get(0).getRating()
        );
    }

    @Test
    void shouldReturnMostRecentReview() {

        Review oldReview = Review.builder()
                .rating(3)
                .comment("Old Review")
                .reviewDate(LocalDate.of(2025,1,1))
                .build();

        Review latestReview = Review.builder()
                .rating(5)
                .comment("Latest Review")
                .reviewDate(LocalDate.of(2027,1,1))
                .build();

        reviewRepository.save(oldReview);

        reviewRepository.save(latestReview);

        Optional<Review> recentReview =
                reviewRepository
                        .findFirstByOrderByReviewDateDesc();

        assertTrue(recentReview.isPresent());

        assertEquals(
                "Latest Review",
                recentReview.get().getComment()
        );
    }

    @Test
    void shouldDeleteReviewSuccessfully() {

        Review review = Review.builder()
                .rating(2)
                .comment("Bad Service")
                .reviewDate(LocalDate.now())
                .build();

        Review savedReview =
                reviewRepository.save(review);

        reviewRepository.deleteById(
                savedReview.getReviewId()
        );

        Optional<Review> deletedReview =
                reviewRepository.findById(
                        savedReview.getReviewId()
                );

        assertFalse(deletedReview.isPresent());
    }

    @Test
    void shouldReturnReviewsByReservationId() {

        Reservation reservation = Reservation.builder()
                .guestName("Surya")
                .guestEmail("surya@gmail.com")
                .guestPhone("9876543210")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(2))
                .build();

        reservation =
                reservationRepository.save(reservation);

        Review review = Review.builder()
                .reservation(reservation)
                .rating(5)
                .comment("Excellent Stay")
                .reviewDate(LocalDate.now())
                .build();

        reviewRepository.save(review);

        Page<Review> reviews =
                reviewRepository.findByReservation_ReservationId(
                        reservation.getReservationId(),
                        PageRequest.of(0,5)
                );

        assertFalse(reviews.isEmpty());

        assertEquals(
                reservation.getReservationId(),
                reviews.getContent()
                        .get(0)
                        .getReservation()
                        .getReservationId()
        );
    }

    @Test
    void shouldCheckIfReviewExistsForReservation() {

        Reservation reservation = Reservation.builder()
                .guestName("Rahul")
                .guestEmail("rahul@gmail.com")
                .guestPhone("9999999999")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(1))
                .build();

        reservation =
                reservationRepository.save(reservation);

        Review review = Review.builder()
                .reservation(reservation)
                .rating(4)
                .comment("Nice Hotel")
                .reviewDate(LocalDate.now())
                .build();

        reviewRepository.save(review);

        boolean exists =
                reviewRepository
                        .existsByReservation_ReservationId(
                                reservation.getReservationId()
                        );

        assertTrue(exists);
    }
}
