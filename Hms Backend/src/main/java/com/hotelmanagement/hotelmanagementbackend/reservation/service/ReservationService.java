package com.hotelmanagement.hotelmanagementbackend.reservation.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationRequestDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationResponseDto;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface ReservationService {

    ReservationResponseDto createReservation(ReservationRequestDto dto);

    ReservationResponseDto getReservationById(Integer reservationId);

    PagedResponse<ReservationResponseDto> getAllReservations(Pageable pageable);

    PagedResponse<ReservationResponseDto> getReservationsByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable);

    ReservationResponseDto updateReservation(Integer reservationId, ReservationRequestDto dto);

    void deleteReservation(Integer reservationId);

    long getTotalReservations();
}
