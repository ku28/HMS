package com.hotelmanagement.hotelmanagementbackend.room.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomAmenityRequestDto {

    @NotNull(message = "Room ID is required")
    private Integer roomId;

    @NotNull(message = "Amenity ID is required")
    private Integer amenityId;
}
