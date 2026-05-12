package com.hotelmanagement.hotelmanagementbackend.room.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeRequestDto {

    @NotBlank(message = "Type name is required")
    @Size(max = 255, message = "Type name must be less than 255 characters")
    private String typeName;

    private String description;

    @NotNull(message = "Max occupancy is required")
    @Positive(message = "Max occupancy must be a positive number")
    private Integer maxOccupancy;

    @NotNull(message = "Price per night is required")
    @Positive(message = "Price per night must be a positive number")
    private BigDecimal pricePerNight;
}
