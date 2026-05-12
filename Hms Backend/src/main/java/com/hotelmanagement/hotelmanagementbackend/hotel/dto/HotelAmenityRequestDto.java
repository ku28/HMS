package com.hotelmanagement.hotelmanagementbackend.hotel.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelAmenityRequestDto {

    @NotNull(message = "Hotel ID is required")
    private Integer hotelId;

    @NotNull(message = "Amenity ID is required")
    private Integer amenityId;
}
