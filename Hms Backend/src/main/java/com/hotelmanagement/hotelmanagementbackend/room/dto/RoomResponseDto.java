package com.hotelmanagement.hotelmanagementbackend.room.dto;

import com.hotelmanagement.hotelmanagementbackend.hotel.dto.AmenityResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponseDto implements Serializable {

    private Integer roomId;
    private Integer roomNumber;
    private Integer hotelId;
    private String hotelName;
    private RoomTypeResponseDto roomType;
    private Boolean isAvailable;
    private List<AmenityResponseDto> amenities;
}
