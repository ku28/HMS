package com.hotelmanagement.hotelmanagementbackend.hotel.dto;

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
public class HotelResponseDto implements Serializable {

    private Integer hotelId;
    private String name;
    private String location;
    private String description;
    private List<AmenityResponseDto> amenities;
}
