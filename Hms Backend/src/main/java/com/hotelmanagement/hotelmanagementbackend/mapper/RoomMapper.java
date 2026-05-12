package com.hotelmanagement.hotelmanagementbackend.mapper;

import com.hotelmanagement.hotelmanagementbackend.hotel.dto.AmenityResponseDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomResponseDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomTypeRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomTypeResponseDto;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class RoomMapper {

    public RoomTypeResponseDto toRoomTypeResponseDto(RoomType roomType) {
        if (roomType == null) return null;
        return RoomTypeResponseDto.builder()
                .roomTypeId(roomType.getRoomTypeId())
                .typeName(roomType.getTypeName())
                .description(roomType.getDescription())
                .maxOccupancy(roomType.getMaxOccupancy())
                .pricePerNight(roomType.getPricePerNight())
                .build();
    }

    public RoomType toRoomTypeEntity(RoomTypeRequestDto dto) {
        if (dto == null) return null;
        return RoomType.builder()
                .typeName(dto.getTypeName())
                .description(dto.getDescription())
                .maxOccupancy(dto.getMaxOccupancy())
                .pricePerNight(dto.getPricePerNight())
                .build();
    }

    public void updateRoomTypeEntity(RoomType roomType, RoomTypeRequestDto dto) {
        if (dto.getTypeName() != null) roomType.setTypeName(dto.getTypeName());
        if (dto.getDescription() != null) roomType.setDescription(dto.getDescription());
        if (dto.getMaxOccupancy() != null) roomType.setMaxOccupancy(dto.getMaxOccupancy());
        if (dto.getPricePerNight() != null) roomType.setPricePerNight(dto.getPricePerNight());
    }

    public RoomResponseDto toRoomResponseDto(Room room) {
        if (room == null) return null;
        return RoomResponseDto.builder()
                .roomId(room.getRoomId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType() != null ? toRoomTypeResponseDto(room.getRoomType()) : null)
                .isAvailable(room.getIsAvailable())
                .amenities(room.getAmenities() != null
                        ? room.getAmenities().stream()
                        .map(a -> AmenityResponseDto.builder()
                                .amenityId(a.getAmenityId())
                                .name(a.getName())
                                .description(a.getDescription())
                                .build())
                        .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

    public RoomResponseDto toRoomResponseDtoWithoutAmenities(Room room) {
        if (room == null) return null;
        return RoomResponseDto.builder()
                .roomId(room.getRoomId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType() != null ? toRoomTypeResponseDto(room.getRoomType()) : null)
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
