package com.hotelmanagement.hotelmanagementbackend.hotel.projection;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "hotelPublic", types = Hotel.class)
public interface HotelPublicProjection {

    Integer getHotelId();

    String getName();

    String getLocation();

    String getDescription();
}
