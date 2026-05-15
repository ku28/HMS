package com.hotelmanagement.hotelmanagementbackend.room.service;

import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomAmenityRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomResponseDto;

public interface RoomService {

    RoomResponseDto createRoom(RoomRequestDto dto);

    RoomResponseDto updateRoom(Integer roomId, RoomRequestDto dto);

    void deleteRoom(Integer roomId);

    void addAmenityToRoom(RoomAmenityRequestDto dto);
}
