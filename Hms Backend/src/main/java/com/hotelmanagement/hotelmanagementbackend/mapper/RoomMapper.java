package com.hotelmanagement.hotelmanagementbackend.mapper;

import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomResponseDto;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class RoomMapper {

    public RoomResponseDto toRoomResponseDto(Room room) {
        if (room == null) return null;
        return RoomResponseDto.builder()
                .roomId(room.getRoomId())
                .roomNumber(room.getRoomNumber())
                .hotelId(room.getHotel() != null ? room.getHotel().getHotelId() : null)
                .hotelName(room.getHotel() != null ? room.getHotel().getName() : null)
                .roomTypeId(room.getRoomType() != null ? room.getRoomType().getRoomTypeId() : null)
                .roomTypeName(room.getRoomType() != null ? room.getRoomType().getTypeName() : null)
                .maxOccupancy(room.getRoomType() != null ? room.getRoomType().getMaxOccupancy() : null)
                .pricePerNight(room.getRoomType() != null ? room.getRoomType().getPricePerNight() : null)
                .isAvailable(room.getIsAvailable())
                .amenities(room.getAmenities() != null
                        ? room.getAmenities().stream()
                        .map(a -> a.getName())
                        .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

    public RoomResponseDto toRoomResponseDtoWithoutAmenities(Room room) {
        if (room == null) return null;
        return RoomResponseDto.builder()
                .roomId(room.getRoomId())
                .roomNumber(room.getRoomNumber())
                .hotelId(room.getHotel() != null ? room.getHotel().getHotelId() : null)
                .hotelName(room.getHotel() != null ? room.getHotel().getName() : null)
                .roomTypeId(room.getRoomType() != null ? room.getRoomType().getRoomTypeId() : null)
                .roomTypeName(room.getRoomType() != null ? room.getRoomType().getTypeName() : null)
                .maxOccupancy(room.getRoomType() != null ? room.getRoomType().getMaxOccupancy() : null)
                .pricePerNight(room.getRoomType() != null ? room.getRoomType().getPricePerNight() : null)
                .isAvailable(room.getIsAvailable())
                .amenities(Collections.emptyList())
                .build();
    }

    public Room toRoomEntity(RoomRequestDto dto) {
        if (dto == null) return null;
        return Room.builder()
                .roomNumber(dto.getRoomNumber())
                .isAvailable(dto.getIsAvailable())
                .build();
    }

    public void updateRoomEntity(Room room, RoomRequestDto dto) {
        if (dto.getRoomNumber() != null) room.setRoomNumber(dto.getRoomNumber());
        if (dto.getIsAvailable() != null) room.setIsAvailable(dto.getIsAvailable());
    }
}
