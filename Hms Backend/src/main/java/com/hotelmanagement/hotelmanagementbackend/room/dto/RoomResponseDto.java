package com.hotelmanagement.hotelmanagementbackend.room.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private Integer roomTypeId;
    private String roomTypeName;
    private Integer maxOccupancy;
    private BigDecimal pricePerNight;
    private Boolean isAvailable;
    private List<String> amenities;
}
