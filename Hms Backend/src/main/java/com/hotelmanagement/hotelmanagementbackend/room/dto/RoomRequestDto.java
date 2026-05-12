package com.hotelmanagement.hotelmanagementbackend.room.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequestDto {

    @NotNull(message = "Room number is required")
    @Positive(message = "Room number must be positive")
    private Integer roomNumber;

    @NotNull(message = "Room type ID is required")
    private Integer roomTypeId;

    @NotNull(message = "Availability status is required")
    private Boolean isAvailable;
}
