package com.hotelmanagement.hotelmanagementbackend.mapper;

import com.hotelmanagement.hotelmanagementbackend.hotel.dto.AmenityRequestDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.AmenityResponseDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.HotelRequestDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.HotelResponseDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class HotelMapper {

    public HotelResponseDto toResponseDto(Hotel hotel) {
        if (hotel == null) return null;
        return HotelResponseDto.builder()
                .hotelId(hotel.getHotelId())
                .name(hotel.getName())
                .location(hotel.getLocation())
                .description(hotel.getDescription())
                .amenities(hotel.getAmenities() != null
                        ? hotel.getAmenities().stream()
                        .map(this::toAmenityResponseDto)
                        .collect(Collectors.toList())
                        : Collections.emptyList())
                .build();
    }

    public HotelResponseDto toResponseDtoWithoutAmenities(Hotel hotel) {
        if (hotel == null) return null;
        return HotelResponseDto.builder()
                .hotelId(hotel.getHotelId())
                .name(hotel.getName())
                .location(hotel.getLocation())
                .description(hotel.getDescription())
                .amenities(Collections.emptyList())
                .build();
    }

    public Hotel toEntity(HotelRequestDto dto) {
        if (dto == null) return null;
        return Hotel.builder()
                .name(dto.getName())
                .location(dto.getLocation())
                .description(dto.getDescription())
                .build();
    }

    public void updateEntity(Hotel hotel, HotelRequestDto dto) {
        if (dto.getName() != null) hotel.setName(dto.getName());
        if (dto.getLocation() != null) hotel.setLocation(dto.getLocation());
        if (dto.getDescription() != null) hotel.setDescription(dto.getDescription());
    }

    public AmenityResponseDto toAmenityResponseDto(Amenity amenity) {
        if (amenity == null) return null;
        return AmenityResponseDto.builder()
                .amenityId(amenity.getAmenityId())
                .name(amenity.getName())
                .description(amenity.getDescription())
                .build();
    }

    public Amenity toAmenityEntity(AmenityRequestDto dto) {
        if (dto == null) return null;
        return Amenity.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .build();
    }

    public void updateAmenityEntity(Amenity amenity, AmenityRequestDto dto) {
        if (dto.getName() != null) amenity.setName(dto.getName());
        if (dto.getDescription() != null) amenity.setDescription(dto.getDescription());
    }
}
