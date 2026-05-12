package com.hotelmanagement.hotelmanagementbackend.room.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.room.dto.*;
import org.springframework.data.domain.Pageable;

public interface RoomTypeService {

    RoomTypeResponseDto createRoomType(RoomTypeRequestDto dto);

    RoomTypeResponseDto getRoomTypeById(Integer roomTypeId);

    PagedResponse<RoomTypeResponseDto> getAllRoomTypes(Pageable pageable);

    RoomTypeResponseDto updateRoomType(Integer roomTypeId, RoomTypeRequestDto dto);

    void deleteRoomType(Integer roomTypeId);
}
