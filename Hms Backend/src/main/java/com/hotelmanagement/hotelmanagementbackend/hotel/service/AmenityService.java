package com.hotelmanagement.hotelmanagementbackend.hotel.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.AmenityRequestDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.AmenityResponseDto;
import org.springframework.data.domain.Pageable;

public interface AmenityService {

    AmenityResponseDto createAmenity(AmenityRequestDto dto);

    AmenityResponseDto getAmenityById(Integer amenityId);

    PagedResponse<AmenityResponseDto> getAllAmenities(Pageable pageable);

    AmenityResponseDto updateAmenity(Integer amenityId, AmenityRequestDto dto);

    void deleteAmenity(Integer amenityId);

    PagedResponse<AmenityResponseDto> getAmenitiesByHotel(Integer hotelId, Pageable pageable);

    PagedResponse<AmenityResponseDto> getAmenitiesByRoom(Integer roomId, Pageable pageable);
}
