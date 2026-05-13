package com.hotelmanagement.hotelmanagementbackend.reservation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {

    @NotBlank(message = "Guest name is required")
    @Size(max = 255, message = "Guest name must be less than 255 characters")
    private String guestName;

    @NotBlank(message = "Guest email is required")
    @Email(message = "Invalid email format")
    @Size(max = 255, message = "Guest email must be less than 255 characters")
    private String guestEmail;

    @NotBlank(message = "Guest phone is required")
    @Size(max = 20, message = "Guest phone must be less than 20 characters")
    private String guestPhone;

    @NotNull(message = "Check-in date is required")
    private LocalDate checkInDate;

    @NotNull(message = "Check-out date is required")
    private LocalDate checkOutDate;

    @NotNull(message = "Room ID is required")
    private Integer roomId;
}
