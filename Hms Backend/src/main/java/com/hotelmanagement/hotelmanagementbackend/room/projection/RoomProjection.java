package com.hotelmanagement.hotelmanagementbackend.room.projection;

import com.hotelmanagement.hotelmanagementbackend.hotel.projection.AmenityProjection;
import com.hotelmanagement.hotelmanagementbackend.hotel.projection.HotelPublicProjection;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import org.springframework.data.rest.core.config.Projection;

import java.util.Set;

@Projection(name = "room", types = Room.class)
public interface RoomProjection {

    Integer getRoomId();

    Integer getRoomNumber();

    HotelPublicProjection getHotel();

    RoomTypeProjection getRoomType();

    Boolean getIsAvailable();

    Set<AmenityProjection> getAmenities();
}
