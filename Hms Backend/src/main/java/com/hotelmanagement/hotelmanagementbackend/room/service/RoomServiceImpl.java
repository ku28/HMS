package com.hotelmanagement.hotelmanagementbackend.room.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponseMapper;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.AmenityRepository;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.HotelRepository;
import com.hotelmanagement.hotelmanagementbackend.mapper.RoomMapper;
import com.hotelmanagement.hotelmanagementbackend.room.dto.*;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import com.hotelmanagement.hotelmanagementbackend.room.repository.RoomRepository;
import com.hotelmanagement.hotelmanagementbackend.room.repository.RoomTypeRepository;
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
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomTypeRepository roomTypeRepository;
    private final AmenityRepository amenityRepository;
    private final HotelRepository hotelRepository;
    private final RoomMapper roomMapper;

    public RoomServiceImpl(RoomRepository roomRepository,
                           RoomTypeRepository roomTypeRepository,
                           AmenityRepository amenityRepository,
                           HotelRepository hotelRepository,
                           RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.roomTypeRepository = roomTypeRepository;
        this.amenityRepository = amenityRepository;
        this.hotelRepository = hotelRepository;
        this.roomMapper = roomMapper;
    }

    @Override
    @CacheEvict(value = "rooms", allEntries = true)
    public RoomResponseDto createRoom(RoomRequestDto dto) {
        if (roomRepository.existsByRoomNumberAndRoomType_RoomTypeId(dto.getRoomNumber(), dto.getRoomTypeId())) {
            throw new ResourceAlreadyExistsException("Room", "roomNumber", dto.getRoomNumber());
        }
        RoomType roomType = roomTypeRepository.findById(dto.getRoomTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", "roomTypeId", dto.getRoomTypeId()));
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel", "hotelId", dto.getHotelId()));
        Room room = roomMapper.toRoomEntity(dto);
        room.setRoomType(roomType);
        room.setHotel(hotel);
        Room saved = roomRepository.save(room);
        return roomMapper.toRoomResponseDtoWithoutAmenities(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "rooms", key = "#roomId")
    public RoomResponseDto getRoomById(Integer roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId));
        return roomMapper.toRoomResponseDto(room);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RoomResponseDto> getAllRooms(Pageable pageable) {
        Page<Room> page = roomRepository.findAll(pageable);
        List<RoomResponseDto> dtos = page.getContent().stream()
                .map(roomMapper::toRoomResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RoomResponseDto> getAvailableRoomsByType(Integer roomTypeId, Pageable pageable) {
        if (!roomTypeRepository.existsById(roomTypeId)) {
            throw new ResourceNotFoundException("RoomType", "roomTypeId", roomTypeId);
        }
        Page<Room> page = roomRepository.findByRoomType_RoomTypeIdAndIsAvailableTrue(roomTypeId, pageable);
        List<RoomResponseDto> dtos = page.getContent().stream()
                .map(roomMapper::toRoomResponseDtoWithoutAmenities)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RoomResponseDto> getRoomsByAmenity(Integer amenityId, Pageable pageable) {
        if (!amenityRepository.existsById(amenityId)) {
            throw new ResourceNotFoundException("Amenity", "amenityId", amenityId);
        }
        Page<Room> page = roomRepository.findByAmenities_AmenityId(amenityId, pageable);
        List<RoomResponseDto> dtos = page.getContent().stream()
                .map(roomMapper::toRoomResponseDtoWithoutAmenities)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @CacheEvict(value = "rooms", allEntries = true)
    public RoomResponseDto updateRoom(Integer roomId, RoomRequestDto dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", roomId));
        roomMapper.updateRoomEntity(room, dto);
        if (dto.getRoomTypeId() != null) {
            RoomType roomType = roomTypeRepository.findById(dto.getRoomTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("RoomType", "roomTypeId", dto.getRoomTypeId()));
            room.setRoomType(roomType);
        }
        Room updated = roomRepository.save(room);
        return roomMapper.toRoomResponseDtoWithoutAmenities(updated);
    }

    @Override
    @CacheEvict(value = "rooms", allEntries = true)
    public void deleteRoom(Integer roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ResourceNotFoundException("Room", "roomId", roomId);
        }
        roomRepository.deleteById(roomId);
    }

    @Override
    @CacheEvict(value = "rooms", allEntries = true)
    public void addAmenityToRoom(RoomAmenityRequestDto dto) {
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException("Room", "roomId", dto.getRoomId()));
        Amenity amenity = amenityRepository.findById(dto.getAmenityId())
                .orElseThrow(() -> new ResourceNotFoundException("Amenity", "amenityId", dto.getAmenityId()));
        room.getAmenities().add(amenity);
        roomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RoomResponseDto> getRoomsByHotel(Integer hotelId, Pageable pageable) {
        Page<Room> page = roomRepository.findByHotel_HotelId(hotelId, pageable);
        List<RoomResponseDto> dtos = page.getContent().stream()
                .map(roomMapper::toRoomResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }
}
