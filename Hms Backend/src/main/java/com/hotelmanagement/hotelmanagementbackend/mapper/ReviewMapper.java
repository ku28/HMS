package com.hotelmanagement.hotelmanagementbackend.mapper;

import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewRequestDto;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewResponseDto;
import com.hotelmanagement.hotelmanagementbackend.review.entity.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponseDto toResponseDto(Review review) {
        if (review == null) return null;
        Reservation reservation = review.getReservation();
        String hotelName = null;
        String guestName = null;
        if (reservation != null) {
            guestName = reservation.getGuestName();
            if (reservation.getRoom() != null && reservation.getRoom().getHotel() != null) {
                hotelName = reservation.getRoom().getHotel().getName();
            }
        }
        return ReviewResponseDto.builder()
                .reviewId(review.getReviewId())
                .reservationId(reservation != null ? reservation.getReservationId() : null)
                .rating(review.getRating())
                .comment(review.getComment())
                .reviewDate(review.getReviewDate())
                .hotelName(hotelName)
                .guestName(guestName)
                .build();
    }

    public Review toEntity(ReviewRequestDto dto) {
        if (dto == null) return null;
        return Review.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .reviewDate(dto.getReviewDate())
                .build();
    }

    public void updateEntity(Review review, ReviewRequestDto dto) {
        if (dto.getRating() != null) review.setRating(dto.getRating());
        if (dto.getComment() != null) review.setComment(dto.getComment());
        if (dto.getReviewDate() != null) review.setReviewDate(dto.getReviewDate());
    }
}
