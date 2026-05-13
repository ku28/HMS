package com.hotelmanagement.hotelmanagementbackend.room.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.room.dto.*;
import org.springframework.data.domain.Pageable;

public interface RoomService {

    RoomResponseDto createRoom(RoomRequestDto dto);

    RoomResponseDto getRoomById(Integer roomId);

    PagedResponse<RoomResponseDto> getAllRooms(Pageable pageable);

    PagedResponse<RoomResponseDto> getAvailableRoomsByType(Integer roomTypeId, Pageable pageable);

    PagedResponse<RoomResponseDto> getRoomsByAmenity(Integer amenityId, Pageable pageable);

    RoomResponseDto updateRoom(Integer roomId, RoomRequestDto dto);

    void deleteRoom(Integer roomId);

    void addAmenityToRoom(RoomAmenityRequestDto dto);

    PagedResponse<RoomResponseDto> getRoomsByHotel(Integer hotelId, Pageable pageable);
}
