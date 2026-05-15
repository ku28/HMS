package com.hotelmanagement.hotelmanagementbackend.review.projection;

import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.room.projection.RoomSummaryProjection;
import org.springframework.data.rest.core.config.Projection;

import java.time.LocalDate;

@Projection(name = "reservationReview", types = Reservation.class)
public interface ReservationReviewProjection {

    Integer getReservationId();

    LocalDate getCheckInDate();

    LocalDate getCheckOutDate();

    RoomSummaryProjection getRoom();
}
