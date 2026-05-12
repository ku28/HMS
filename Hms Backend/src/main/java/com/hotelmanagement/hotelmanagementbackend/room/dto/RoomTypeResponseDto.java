package com.hotelmanagement.hotelmanagementbackend.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomTypeResponseDto implements Serializable {

    private Integer roomTypeId;
    private String typeName;
    private String description;
    private Integer maxOccupancy;
    private BigDecimal pricePerNight;
}
