package com.hotelmanagement.hotelmanagementbackend.reservation.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.exception.BadRequestException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.mapper.ReservationMapper;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationRequestDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationResponseDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.reservation.repository.ReservationRepository;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import com.hotelmanagement.hotelmanagementbackend.room.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReservationService Unit Tests")
class ReservationServiceImplTest {

    @Mock private ReservationRepository reservationRepository;
    @Mock private RoomRepository roomRepository;
    @Mock private ReservationMapper reservationMapper;
    @InjectMocks private ReservationServiceImpl reservationService;

    private Reservation testReservation;
    private ReservationRequestDto testRequestDto;
    private ReservationResponseDto testResponseDto;
    private Room testRoom;

    @BeforeEach
    void setUp() {
        testRoom = Room.builder().roomId(1).roomNumber(101)
                .roomType(RoomType.builder().roomTypeId(1).typeName("Deluxe").build())
                .isAvailable(true).build();
        testReservation = Reservation.builder().reservationId(1).guestName("John Doe")
                .guestEmail("john@example.com").guestPhone("+1234567890")
                .checkInDate(LocalDate.now().plusDays(7)).checkOutDate(LocalDate.now().plusDays(10))
                .room(testRoom).build();
        testRequestDto = ReservationRequestDto.builder().guestName("John Doe")
                .guestEmail("john@example.com").guestPhone("+1234567890")
                .checkInDate(LocalDate.now().plusDays(7)).checkOutDate(LocalDate.now().plusDays(10))
                .roomId(1).build();
        testResponseDto = ReservationResponseDto.builder().reservationId(1).guestName("John Doe")
                .guestEmail("john@example.com").guestPhone("+1234567890")
                .checkInDate(LocalDate.now().plusDays(7)).checkOutDate(LocalDate.now().plusDays(10))
                .roomId(1).roomNumber(101).roomTypeName("Deluxe").build();
    }

    @Test @DisplayName("shouldCreateReservationSuccessfully")
    void shouldCreateReservationSuccessfully() {
        when(roomRepository.findById(1)).thenReturn(Optional.of(testRoom));
        when(reservationRepository.existsByRoom_RoomIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
                anyInt(), any(), any())).thenReturn(false);
        when(reservationMapper.toEntity(testRequestDto)).thenReturn(testReservation);
        when(reservationRepository.save(any())).thenReturn(testReservation);
        when(reservationMapper.toResponseDto(testReservation)).thenReturn(testResponseDto);

        ReservationResponseDto result = reservationService.createReservation(testRequestDto);
        assertThat(result.getReservationId()).isEqualTo(1);
        assertThat(result.getGuestName()).isEqualTo("John Doe");
        verify(reservationRepository).save(any());
    }

    @Test @DisplayName("shouldRejectInvalidReservationDates")
    void shouldRejectInvalidReservationDates() {
        ReservationRequestDto bad = ReservationRequestDto.builder().guestName("Jane")
                .guestEmail("jane@x.com").guestPhone("123").roomId(1)
                .checkInDate(LocalDate.now().plusDays(10)).checkOutDate(LocalDate.now().plusDays(5)).build();
        assertThatThrownBy(() -> reservationService.createReservation(bad))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Check-in date must be before check-out date");
        verify(reservationRepository, never()).save(any());
    }

    @Test @DisplayName("shouldRejectReservationWhenRoomAlreadyBooked")
    void shouldRejectReservationWhenRoomAlreadyBooked() {
        when(roomRepository.findById(1)).thenReturn(Optional.of(testRoom));
        when(reservationRepository.existsByRoom_RoomIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
                anyInt(), any(), any())).thenReturn(true);
        assertThatThrownBy(() -> reservationService.createReservation(testRequestDto))
                .isInstanceOf(BadRequestException.class).hasMessageContaining("already booked");
    }

    @Test @DisplayName("shouldThrowExceptionWhenRoomNotFound")
    void shouldThrowExceptionWhenRoomNotFound() {
        ReservationRequestDto dto = ReservationRequestDto.builder().guestName("X").guestEmail("x@x.com")
                .guestPhone("1").checkInDate(LocalDate.now().plusDays(1))
                .checkOutDate(LocalDate.now().plusDays(3)).roomId(999).build();
        when(roomRepository.findById(999)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reservationService.createReservation(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("shouldReturnReservationById")
    void shouldReturnReservationById() {
        when(reservationRepository.findById(1)).thenReturn(Optional.of(testReservation));
        when(reservationMapper.toResponseDto(testReservation)).thenReturn(testResponseDto);
        ReservationResponseDto result = reservationService.getReservationById(1);
        assertThat(result.getGuestEmail()).isEqualTo("john@example.com");
    }

    @Test @DisplayName("shouldThrowExceptionWhenReservationNotFound")
    void shouldThrowExceptionWhenReservationNotFound() {
        when(reservationRepository.findById(999)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> reservationService.getReservationById(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("shouldDeleteReservationSuccessfully")
    void shouldDeleteReservationSuccessfully() {
        when(reservationRepository.existsById(1)).thenReturn(true);
        reservationService.deleteReservation(1);
        verify(reservationRepository).deleteById(1);
    }

    @Test @DisplayName("shouldThrowExceptionWhenDeletingNonExistentReservation")
    void shouldThrowExceptionWhenDeletingNonExistentReservation() {
        when(reservationRepository.existsById(999)).thenReturn(false);
        assertThatThrownBy(() -> reservationService.deleteReservation(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("shouldReturnPaginatedReservationsByDateRange")
    void shouldReturnPaginatedReservationsByDateRange() {
        Pageable p = PageRequest.of(0, 10);
        LocalDate s = LocalDate.now(), e = LocalDate.now().plusDays(30);
        Page<Reservation> page = new PageImpl<>(List.of(testReservation), p, 1);
        when(reservationRepository.findByCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual(s, e, p))
                .thenReturn(page);
        when(reservationMapper.toResponseDto(testReservation)).thenReturn(testResponseDto);
        PagedResponse<ReservationResponseDto> result = reservationService.getReservationsByDateRange(s, e, p);
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test @DisplayName("shouldReturnTotalReservationCount")
    void shouldReturnTotalReservationCount() {
        when(reservationRepository.count()).thenReturn(42L);
        assertThat(reservationService.getTotalReservations()).isEqualTo(42);
    }
}
