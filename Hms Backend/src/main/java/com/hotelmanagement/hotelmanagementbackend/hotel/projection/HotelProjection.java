package com.hotelmanagement.hotelmanagementbackend.hotel.projection;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import org.springframework.data.rest.core.config.Projection;

import java.util.Set;

@Projection(name = "hotel", types = Hotel.class)
public interface HotelProjection {

    Integer getHotelId();

    String getName();

    String getLocation();

    String getDescription();

    Set<AmenityProjection> getAmenities();
}
