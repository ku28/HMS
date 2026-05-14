package com.hotelmanagement.hotelmanagementbackend.review.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.review.entity.Review;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryDataJpaTest
@DisplayName("ReviewRepository Tests")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByRating should return reviews with rating")
    void findByRatingShouldReturnReviewsWithRating() {
        Reservation firstReservation = persistReservation(101, "first-review@example.com");
        Reservation secondReservation = persistReservation(102, "second-review@example.com");
        persistReview(firstReservation, 5, LocalDate.of(2026, 12, 1));
        persistReview(secondReservation, 3, LocalDate.of(2026, 12, 2));

        Page<Review> result = reviewRepository.findByRating(5, PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Review::getRating)
                .containsExactly(5);
    }

    @Test
    @DisplayName("findByReservation_ReservationId should return review for reservation")
    void findByReservationReservationIdShouldReturnReviewForReservation() {
        Reservation firstReservation = persistReservation(201, "first-reservation-review@example.com");
        Reservation secondReservation = persistReservation(202, "second-reservation-review@example.com");
        persistReview(firstReservation, 4, LocalDate.of(2026, 12, 3));
        persistReview(secondReservation, 5, LocalDate.of(2026, 12, 4));

        Page<Review> result = reviewRepository.findByReservation_ReservationId(
                firstReservation.getReservationId(), PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Review::getRating)
                .containsExactly(4);
    }

    @Test
    @DisplayName("findFirstByOrderByReviewDateDesc should return newest review")
    void findFirstByOrderByReviewDateDescShouldReturnNewestReview() {
        Reservation oldReservation = persistReservation(301, "old-review@example.com");
        Reservation newReservation = persistReservation(302, "new-review@example.com");
        persistReview(oldReservation, 4, LocalDate.of(2026, 12, 5));
        Review newestReview = persistReview(newReservation, 5, LocalDate.of(2026, 12, 10));

        Optional<Review> result = reviewRepository.findFirstByOrderByReviewDateDesc();

        assertThat(result).isPresent();
        assertThat(result.get().getReviewId()).isEqualTo(newestReview.getReviewId());
    }

    @Test
    @DisplayName("existsByReservation_ReservationId should report whether reservation has review")
    void existsByReservationReservationIdShouldReportWhetherReservationHasReview() {
        Reservation reviewedReservation = persistReservation(401, "reviewed@example.com");
        Reservation unreviewedReservation = persistReservation(402, "unreviewed@example.com");
        persistReview(reviewedReservation, 5, LocalDate.of(2026, 12, 11));

        assertThat(reviewRepository.existsByReservation_ReservationId(reviewedReservation.getReservationId())).isTrue();
        assertThat(reviewRepository.existsByReservation_ReservationId(unreviewedReservation.getReservationId())).isFalse();
    }

    private Review persistReview(Reservation reservation, Integer rating, LocalDate reviewDate) {
        return entityManager.persistAndFlush(Review.builder()
                .reservation(reservation)
                .rating(rating)
                .comment("Review comment")
                .reviewDate(reviewDate)
                .build());
    }

    private Reservation persistReservation(Integer roomNumber, String guestEmail) {
        Hotel hotel = entityManager.persistAndFlush(Hotel.builder()
                .name("Review Hotel " + roomNumber)
                .location("Chennai")
                .description("Review test hotel")
                .build());
        RoomType roomType = entityManager.persistAndFlush(RoomType.builder()
                .typeName("Review Type " + roomNumber)
                .description("Review test type")
                .maxOccupancy(2)
                .pricePerNight(new BigDecimal("275.00"))
                .build());
        Room room = entityManager.persistAndFlush(Room.builder()
                .roomNumber(roomNumber)
                .hotel(hotel)
                .roomType(roomType)
                .isAvailable(true)
                .build());
        return entityManager.persistAndFlush(Reservation.builder()
                .guestName("Review Guest")
                .guestEmail(guestEmail)
                .guestPhone("9999999999")
                .room(room)
                .checkInDate(LocalDate.of(2026, 12, 1))
                .checkOutDate(LocalDate.of(2026, 12, 3))
                .build());
    }
}
