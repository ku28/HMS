package com.hotelmanagement.hotelmanagementbackend.hotel.projection;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "amenity", types = Amenity.class)
public interface AmenityProjection {

    Integer getAmenityId();

    String getName();

    String getDescription();
}
