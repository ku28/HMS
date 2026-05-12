package com.hotelmanagement.hotelmanagementbackend.mapper;

import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationRequestDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationResponseDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public ReservationResponseDto toResponseDto(Reservation reservation) {
        if (reservation == null) return null;
        return ReservationResponseDto.builder()
                .reservationId(reservation.getReservationId())
                .guestName(reservation.getGuestName())
                .guestEmail(reservation.getGuestEmail())
                .guestPhone(reservation.getGuestPhone())
                .checkInDate(reservation.getCheckInDate())
                .checkOutDate(reservation.getCheckOutDate())
                .roomId(reservation.getRoom() != null ? reservation.getRoom().getRoomId() : null)
                .roomNumber(reservation.getRoom() != null ? reservation.getRoom().getRoomNumber() : null)
                .roomTypeName(reservation.getRoom() != null && reservation.getRoom().getRoomType() != null
                        ? reservation.getRoom().getRoomType().getTypeName() : null)
                .build();
    }

    public Reservation toEntity(ReservationRequestDto dto) {
        if (dto == null) return null;
        return Reservation.builder()
                .guestName(dto.getGuestName())
                .guestEmail(dto.getGuestEmail())
                .guestPhone(dto.getGuestPhone())
                .checkInDate(dto.getCheckInDate())
                .checkOutDate(dto.getCheckOutDate())
                .build();
    }

    public void updateEntity(Reservation reservation, ReservationRequestDto dto) {
        if (dto.getGuestName() != null) reservation.setGuestName(dto.getGuestName());
        if (dto.getGuestEmail() != null) reservation.setGuestEmail(dto.getGuestEmail());
        if (dto.getGuestPhone() != null) reservation.setGuestPhone(dto.getGuestPhone());
        if (dto.getCheckInDate() != null) reservation.setCheckInDate(dto.getCheckInDate());
        if (dto.getCheckOutDate() != null) reservation.setCheckOutDate(dto.getCheckOutDate());
    }
}
