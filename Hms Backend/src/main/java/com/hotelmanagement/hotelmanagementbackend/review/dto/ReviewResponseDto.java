package com.hotelmanagement.hotelmanagementbackend.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto implements Serializable {

    private Integer reviewId;
    private Integer reservationId;
    private Integer rating;
    private String comment;
    private LocalDate reviewDate;
    private String hotelName;
    private String guestName;
}
