package com.hotelmanagement.hotelmanagementbackend.review.repository;

import com.hotelmanagement.hotelmanagementbackend.review.entity.Review;
import com.hotelmanagement.hotelmanagementbackend.review.projection.ReviewProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RepositoryRestResource(path = "reviews", collectionResourceRel = "reviews",
        excerptProjection = ReviewProjection.class)
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @RestResource(path = "by-rating", rel = "by-rating")
    Page<Review> findByRating(@Param("rating") Integer rating, Pageable pageable);

    @RestResource(path = "by-reservation", rel = "by-reservation")
    Page<Review> findByReservation_ReservationId(@Param("reservationId") Integer reservationId, Pageable pageable);

    @RestResource(path = "by-hotel", rel = "by-hotel")
    Page<Review> findByReservation_Room_Hotel_HotelId(@Param("hotelId") Integer hotelId, Pageable pageable);

    @RestResource(exported = false)
    Optional<Review> findFirstByOrderByReviewDateDesc();

    @RestResource(exported = false)
    boolean existsByReservation_ReservationId(Integer reservationId);

    @RestResource(path = "all", rel = "all")
    Page<Review> findByReviewIdGreaterThan(@Param("reviewId") Integer reviewId, Pageable pageable);

    @Override
    @RestResource(exported = false)
    <S extends Review> S save(S entity);

    @Override
    @RestResource(exported = false)
    void deleteById(Integer integer);

    @Override
    @RestResource(exported = false)
    void delete(Review entity);

    @Override
    @RestResource(exported = false)
    void deleteAllById(Iterable<? extends Integer> integers);

    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends Review> entities);

    @Override
    @RestResource(exported = false)
    void deleteAll();
}
