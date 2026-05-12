package com.hotelmanagement.hotelmanagementbackend.hotel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AmenityResponseDto implements Serializable {

    private Integer amenityId;
    private String name;
    private String description;
}
