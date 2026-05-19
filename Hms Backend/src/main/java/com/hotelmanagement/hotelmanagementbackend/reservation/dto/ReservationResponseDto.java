package com.hotelmanagement.hotelmanagementbackend.reservation.dto;

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
public class ReservationResponseDto implements Serializable {

    private Integer reservationId;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private Integer roomId;
    private Integer roomNumber;
    private String roomTypeName;
    private String hotelName;
}
