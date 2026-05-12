package com.hotelmanagement.hotelmanagementbackend.hotel.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.*;
import org.springframework.data.domain.Pageable;

public interface HotelService {

    HotelResponseDto createHotel(HotelRequestDto dto);

    HotelResponseDto getHotelById(Integer hotelId);

    PagedResponse<HotelResponseDto> searchHotels(String search, Pageable pageable);

    HotelResponseDto updateHotel(Integer hotelId, HotelRequestDto dto);

    void deleteHotel(Integer hotelId);

    PagedResponse<HotelResponseDto> getHotelsByAmenity(Integer amenityId, Pageable pageable);

    void addAmenityToHotel(HotelAmenityRequestDto dto);
}
