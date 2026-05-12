package com.hotelmanagement.hotelmanagementbackend.hotel.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponseMapper;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.AmenityRequestDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.AmenityResponseDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.AmenityRepository;
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
public class AmenityServiceImpl implements AmenityService {

    private final AmenityRepository amenityRepository;
    private final HotelMapper hotelMapper;

    public AmenityServiceImpl(AmenityRepository amenityRepository, HotelMapper hotelMapper) {
        this.amenityRepository = amenityRepository;
        this.hotelMapper = hotelMapper;
    }

    @Override
    @CacheEvict(value = "amenities", allEntries = true)
    public AmenityResponseDto createAmenity(AmenityRequestDto dto) {
        if (amenityRepository.existsByName(dto.getName())) {
            throw new ResourceAlreadyExistsException("Amenity", "name", dto.getName());
        }
        Amenity amenity = hotelMapper.toAmenityEntity(dto);
        Amenity saved = amenityRepository.save(amenity);
        return hotelMapper.toAmenityResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "amenities", key = "#amenityId")
    public AmenityResponseDto getAmenityById(Integer amenityId) {
        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "amenityId", amenityId));
        return hotelMapper.toAmenityResponseDto(amenity);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AmenityResponseDto> getAllAmenities(Pageable pageable) {
        Page<Amenity> page = amenityRepository.findByNameContainingIgnoreCase("", pageable);
        List<AmenityResponseDto> dtos = page.getContent().stream()
                .map(hotelMapper::toAmenityResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @CacheEvict(value = "amenities", allEntries = true)
    public AmenityResponseDto updateAmenity(Integer amenityId, AmenityRequestDto dto) {
        Amenity amenity = amenityRepository.findById(amenityId)
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "amenityId", amenityId));
        hotelMapper.updateAmenityEntity(amenity, dto);
        Amenity updated = amenityRepository.save(amenity);
        return hotelMapper.toAmenityResponseDto(updated);
    }

    @Override
    @CacheEvict(value = "amenities", allEntries = true)
    public void deleteAmenity(Integer amenityId) {
        if (!amenityRepository.existsById(amenityId)) {
            throw new ResourceNotFoundException("Amenity", "amenityId", amenityId);
        }
        amenityRepository.deleteById(amenityId);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AmenityResponseDto> getAmenitiesByHotel(Integer hotelId, Pageable pageable) {
        Page<Amenity> page = amenityRepository.findByHotels_HotelId(hotelId, pageable);
        List<AmenityResponseDto> dtos = page.getContent().stream()
                .map(hotelMapper::toAmenityResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<AmenityResponseDto> getAmenitiesByRoom(Integer roomId, Pageable pageable) {
        Page<Amenity> page = amenityRepository.findByRooms_RoomId(roomId, pageable);
        List<AmenityResponseDto> dtos = page.getContent().stream()
                .map(hotelMapper::toAmenityResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }
}
