package com.hotelmanagement.hotelmanagementbackend.review.projection;

import com.hotelmanagement.hotelmanagementbackend.review.entity.Review;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

@Projection(name = "review", types = Review.class)
public interface ReviewProjection {

    Integer getReviewId();

    ReservationReviewProjection getReservation();

    Integer getRating();

    String getComment();

    LocalDate getReviewDate();
}
