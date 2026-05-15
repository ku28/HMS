package com.hotelmanagement.hotelmanagementbackend.room.projection;

import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import org.springframework.data.rest.core.config.Projection;

@Projection(name = "roomSummary", types = Room.class)
public interface RoomSummaryProjection {

    Integer getRoomId();

    Integer getRoomNumber();

    RoomTypeProjection getRoomType();
}
