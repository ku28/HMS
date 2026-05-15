package com.hotelmanagement.hotelmanagementbackend.room.projection;

import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import org.springframework.data.rest.core.config.Projection;

import java.math.BigDecimal;

@Projection(name = "roomType", types = RoomType.class)
public interface RoomTypeProjection {

    Integer getRoomTypeId();

    String getTypeName();

    String getDescription();

    Integer getMaxOccupancy();

    BigDecimal getPricePerNight();
}
