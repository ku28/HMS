package com.hotelmanagement.hotelmanagementbackend.room.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponseMapper;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.mapper.RoomMapper;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomTypeRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomTypeResponseDto;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
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
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomMapper roomMapper;

    public RoomTypeServiceImpl(RoomTypeRepository roomTypeRepository, RoomMapper roomMapper) {
        this.roomTypeRepository = roomTypeRepository;
        this.roomMapper = roomMapper;
    }

    @Override
    @CacheEvict(value = "roomTypes", allEntries = true)
    public RoomTypeResponseDto createRoomType(RoomTypeRequestDto dto) {
        if (roomTypeRepository.existsByTypeName(dto.getTypeName())) {
            throw new ResourceAlreadyExistsException("RoomType", "typeName", dto.getTypeName());
        }
        RoomType roomType = roomMapper.toRoomTypeEntity(dto);
        RoomType saved = roomTypeRepository.save(roomType);
        return roomMapper.toRoomTypeResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "roomTypes", key = "#roomTypeId")
    public RoomTypeResponseDto getRoomTypeById(Integer roomTypeId) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", "roomTypeId", roomTypeId));
        return roomMapper.toRoomTypeResponseDto(roomType);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<RoomTypeResponseDto> getAllRoomTypes(Pageable pageable) {
        Page<RoomType> page = roomTypeRepository.findByTypeNameContainingIgnoreCase("", pageable);
        List<RoomTypeResponseDto> dtos = page.getContent().stream()
                .map(roomMapper::toRoomTypeResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @CacheEvict(value = "roomTypes", allEntries = true)
    public RoomTypeResponseDto updateRoomType(Integer roomTypeId, RoomTypeRequestDto dto) {
        RoomType roomType = roomTypeRepository.findById(roomTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("RoomType", "roomTypeId", roomTypeId));
        roomMapper.updateRoomTypeEntity(roomType, dto);
        RoomType updated = roomTypeRepository.save(roomType);
        return roomMapper.toRoomTypeResponseDto(updated);
    }

    @Override
    @CacheEvict(value = "roomTypes", allEntries = true)
    public void deleteRoomType(Integer roomTypeId) {
        if (!roomTypeRepository.existsById(roomTypeId)) {
            throw new ResourceNotFoundException("RoomType", "roomTypeId", roomTypeId);
        }
        roomTypeRepository.deleteById(roomTypeId);
    }
}
