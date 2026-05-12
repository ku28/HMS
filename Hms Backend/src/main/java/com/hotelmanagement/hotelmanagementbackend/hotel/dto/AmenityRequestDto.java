package com.hotelmanagement.hotelmanagementbackend.hotel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityRequestDto {

    @NotBlank(message = "Amenity name is required")
    @Size(max = 255, message = "Amenity name must be less than 255 characters")
    private String name;

    private String description;
}
