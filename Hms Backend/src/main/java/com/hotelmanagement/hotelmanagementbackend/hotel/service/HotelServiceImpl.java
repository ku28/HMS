package com.hotelmanagement.hotelmanagementbackend.hotel.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponseMapper;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.*;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.AmenityRepository;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.HotelRepository;
import com.hotelmanagement.hotelmanagementbackend.mapper.HotelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper hotelMapper;

    public HotelServiceImpl(HotelRepository hotelRepository,
                            AmenityRepository amenityRepository,
                            HotelMapper hotelMapper) {
        this.hotelRepository = hotelRepository;
        this.amenityRepository = amenityRepository;
        this.hotelMapper = hotelMapper;
    }

    @Override
    @CacheEvict(value = "hotels", allEntries = true)
    public HotelResponseDto createHotel(HotelRequestDto dto) {
        if (hotelRepository.existsByNameAndLocation(dto.getName(), dto.getLocation())) {
            throw new ResourceAlreadyExistsException("Hotel", "name and location",
                    dto.getName() + ", " + dto.getLocation());
        }
        Hotel hotel = hotelMapper.toEntity(dto);
        Hotel saved = hotelRepository.save(hotel);
        return hotelMapper.toResponseDtoWithoutAmenities(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "hotels", key = "#hotelId")
    public HotelResponseDto getHotelById(Integer hotelId) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "hotelId", hotelId));
        return hotelMapper.toResponseDto(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<HotelResponseDto> searchHotels(String search, Pageable pageable) {
        Page<Hotel> page;
        if (search != null && !search.isBlank()) {
            page = hotelRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                    search, search, pageable);
        } else {
            page = hotelRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                    "", "", pageable);
        }
        List<HotelResponseDto> dtos = page.getContent().stream()
                .map(hotelMapper::toResponseDtoWithoutAmenities)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @CacheEvict(value = "hotels", allEntries = true)
    public HotelResponseDto updateHotel(Integer hotelId, HotelRequestDto dto) {
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "hotelId", hotelId));
        hotelMapper.updateEntity(hotel, dto);
        Hotel updated = hotelRepository.save(hotel);
        return hotelMapper.toResponseDtoWithoutAmenities(updated);
    }

    @Override
    @CacheEvict(value = "hotels", allEntries = true)
    public void deleteHotel(Integer hotelId) {
        if (!hotelRepository.existsById(hotelId)) {
            throw new ResourceNotFoundException("Hotel", "hotelId", hotelId);
        }
        hotelRepository.deleteById(hotelId);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<HotelResponseDto> getHotelsByAmenity(Integer amenityId, Pageable pageable) {
        if (!amenityRepository.existsById(amenityId)) {
            throw new ResourceNotFoundException("Amenity", "amenityId", amenityId);
        }
        Page<Hotel> page = hotelRepository.findByAmenities_AmenityId(amenityId, pageable);
        List<HotelResponseDto> dtos = page.getContent().stream()
                .map(hotelMapper::toResponseDtoWithoutAmenities)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @CacheEvict(value = "hotels", allEntries = true)
    public void addAmenityToHotel(HotelAmenityRequestDto dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "hotelId", dto.getHotelId()));
        Amenity amenity = amenityRepository.findById(dto.getAmenityId())
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "amenityId", dto.getAmenityId()));
        hotel.getAmenities().add(amenity);
        hotelRepository.save(hotel);
    }
}
