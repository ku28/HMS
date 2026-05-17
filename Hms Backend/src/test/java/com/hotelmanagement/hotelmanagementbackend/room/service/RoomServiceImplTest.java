package com.hotelmanagement.hotelmanagementbackend.room.service;

import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.AmenityRepository;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.HotelRepository;
import com.hotelmanagement.hotelmanagementbackend.mapper.RoomMapper;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomAmenityRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomResponseDto;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import com.hotelmanagement.hotelmanagementbackend.room.repository.RoomRepository;
import com.hotelmanagement.hotelmanagementbackend.room.repository.RoomTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomService Unit Tests")
class RoomServiceImplTest {

    @Mock private RoomRepository roomRepository;
    @Mock private RoomTypeRepository roomTypeRepository;
    @Mock private AmenityRepository amenityRepository;
    @Mock private HotelRepository hotelRepository;
    @Mock private RoomMapper roomMapper;
    @InjectMocks private RoomServiceImpl roomService;

    private Hotel hotel;
    private RoomType roomType;
    private Room room;
    private RoomRequestDto requestDto;
    private RoomResponseDto responseDto;

    @BeforeEach
    void setUp() {
        hotel = Hotel.builder()
                .hotelId(1)
                .name("Test Hotel")
                .location("Delhi")
                .build();
        roomType = RoomType.builder()
                .roomTypeId(2)
                .typeName("Deluxe")
                .maxOccupancy(3)
                .pricePerNight(new BigDecimal("2500.00"))
                .build();
        room = Room.builder()
                .roomId(10)
                .roomNumber(101)
                .hotel(hotel)
                .roomType(roomType)
                .isAvailable(true)
                .build();
        requestDto = RoomRequestDto.builder()
                .roomNumber(101)
                .hotelId(1)
                .roomTypeId(2)
                .isAvailable(true)
                .build();
        responseDto = RoomResponseDto.builder()
                .roomId(10)
                .roomNumber(101)
                .hotelId(1)
                .hotelName("Test Hotel")
                .roomTypeId(2)
                .roomTypeName("Deluxe")
                .maxOccupancy(3)
                .pricePerNight(new BigDecimal("2500.00"))
                .isAvailable(true)
                .build();
    }

    @Test
    @DisplayName("shouldCreateRoomSuccessfully")
    void shouldCreateRoomSuccessfully() {
        when(roomRepository.existsByRoomNumberAndRoomType_RoomTypeId(101, 2)).thenReturn(false);
        when(roomTypeRepository.findById(2)).thenReturn(Optional.of(roomType));
        when(hotelRepository.findById(1)).thenReturn(Optional.of(hotel));
        when(roomMapper.toRoomEntity(requestDto)).thenReturn(Room.builder()
                .roomNumber(101)
                .isAvailable(true)
                .build());
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        when(roomMapper.toRoomResponseDtoWithoutAmenities(room)).thenReturn(responseDto);

        RoomResponseDto result = roomService.createRoom(requestDto);

        assertThat(result.getRoomId()).isEqualTo(10);
        assertThat(result.getHotelId()).isEqualTo(1);
        assertThat(result.getRoomTypeId()).isEqualTo(2);
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    @DisplayName("shouldRejectDuplicateRoomNumberForRoomType")
    void shouldRejectDuplicateRoomNumberForRoomType() {
        when(roomRepository.existsByRoomNumberAndRoomType_RoomTypeId(101, 2)).thenReturn(true);

        assertThatThrownBy(() -> roomService.createRoom(requestDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);

        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenRoomTypeNotFoundWhileCreating")
    void shouldThrowExceptionWhenRoomTypeNotFoundWhileCreating() {
        when(roomRepository.existsByRoomNumberAndRoomType_RoomTypeId(101, 2)).thenReturn(false);
        when(roomTypeRepository.findById(2)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.createRoom(requestDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(hotelRepository, never()).findById(any());
        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenHotelNotFoundWhileCreating")
    void shouldThrowExceptionWhenHotelNotFoundWhileCreating() {
        when(roomRepository.existsByRoomNumberAndRoomType_RoomTypeId(101, 2)).thenReturn(false);
        when(roomTypeRepository.findById(2)).thenReturn(Optional.of(roomType));
        when(hotelRepository.findById(1)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.createRoom(requestDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldUpdateRoomSuccessfully")
    void shouldUpdateRoomSuccessfully() {
        Hotel updatedHotel = Hotel.builder().hotelId(3).name("Updated Hotel").build();
        RoomType updatedRoomType = RoomType.builder()
                .roomTypeId(4)
                .typeName("Suite")
                .maxOccupancy(4)
                .pricePerNight(new BigDecimal("4200.00"))
                .build();
        RoomRequestDto updateDto = RoomRequestDto.builder()
                .roomNumber(202)
                .hotelId(3)
                .roomTypeId(4)
                .isAvailable(false)
                .build();
        Room updatedRoom = Room.builder()
                .roomId(10)
                .roomNumber(202)
                .hotel(updatedHotel)
                .roomType(updatedRoomType)
                .isAvailable(false)
                .build();
        RoomResponseDto updatedResponse = RoomResponseDto.builder()
                .roomId(10)
                .roomNumber(202)
                .hotelId(3)
                .roomTypeId(4)
                .isAvailable(false)
                .build();

        when(roomRepository.findById(10)).thenReturn(Optional.of(room));
        when(roomTypeRepository.findById(4)).thenReturn(Optional.of(updatedRoomType));
        when(hotelRepository.findById(3)).thenReturn(Optional.of(updatedHotel));
        when(roomRepository.save(room)).thenReturn(updatedRoom);
        when(roomMapper.toRoomResponseDtoWithoutAmenities(updatedRoom)).thenReturn(updatedResponse);

        RoomResponseDto result = roomService.updateRoom(10, updateDto);

        assertThat(result.getRoomNumber()).isEqualTo(202);
        assertThat(result.getHotelId()).isEqualTo(3);
        assertThat(result.getRoomTypeId()).isEqualTo(4);
        assertThat(result.getIsAvailable()).isFalse();
        verify(roomMapper).updateRoomEntity(room, updateDto);
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenUpdatingMissingRoom")
    void shouldThrowExceptionWhenUpdatingMissingRoom() {
        when(roomRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.updateRoom(999, requestDto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(roomRepository, never()).save(any());
    }

    @Test
    @DisplayName("shouldDeleteRoomSuccessfully")
    void shouldDeleteRoomSuccessfully() {
        when(roomRepository.existsById(10)).thenReturn(true);

        roomService.deleteRoom(10);

        verify(roomRepository).deleteById(10);
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenDeletingMissingRoom")
    void shouldThrowExceptionWhenDeletingMissingRoom() {
        when(roomRepository.existsById(999)).thenReturn(false);

        assertThatThrownBy(() -> roomService.deleteRoom(999))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(roomRepository, never()).deleteById(999);
    }

    @Test
    @DisplayName("shouldAddAmenityToRoom")
    void shouldAddAmenityToRoom() {
        Amenity amenity = Amenity.builder()
                .amenityId(7)
                .name("WiFi")
                .build();
        RoomAmenityRequestDto dto = RoomAmenityRequestDto.builder()
                .roomId(10)
                .amenityId(7)
                .build();

        when(roomRepository.findById(10)).thenReturn(Optional.of(room));
        when(amenityRepository.findById(7)).thenReturn(Optional.of(amenity));
        when(roomRepository.save(room)).thenReturn(room);

        roomService.addAmenityToRoom(dto);

        assertThat(room.getAmenities()).contains(amenity);
        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("shouldThrowExceptionWhenAddingMissingAmenity")
    void shouldThrowExceptionWhenAddingMissingAmenity() {
        RoomAmenityRequestDto dto = RoomAmenityRequestDto.builder()
                .roomId(10)
                .amenityId(999)
                .build();

        when(roomRepository.findById(10)).thenReturn(Optional.of(room));
        when(amenityRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> roomService.addAmenityToRoom(dto))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(roomRepository, never()).save(any());
    }
}
