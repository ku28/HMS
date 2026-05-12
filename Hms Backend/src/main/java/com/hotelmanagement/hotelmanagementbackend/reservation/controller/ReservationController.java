package com.hotelmanagement.hotelmanagementbackend.reservation.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationRequestDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationResponseDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.service.ReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reservation")
@Tag(name = "Reservation", description = "Reservation Management APIs")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/post")
    @Operation(summary = "Create a new reservation")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> createReservation(
            @Valid @RequestBody ReservationRequestDto dto) {
        ReservationResponseDto response = reservationService.createReservation(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("POSTSUCCESS", "Reservation added successfully", response));
    }

    @GetMapping("/{reservation_id}")
    @Operation(summary = "Get reservation by ID")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> getReservationById(
            @PathVariable("reservation_id") Integer reservationId) {
        ReservationResponseDto response = reservationService.getReservationById(reservationId);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Reservation retrieved", response));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all reservations")
    public ResponseEntity<ApiResponse<PagedResponse<ReservationResponseDto>>> getAllReservations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "reservationId") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<ReservationResponseDto> response = reservationService.getAllReservations(pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Reservations retrieved", response));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get reservations by date range")
    public ResponseEntity<ApiResponse<PagedResponse<ReservationResponseDto>>> getByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<ReservationResponseDto> response =
                reservationService.getReservationsByDateRange(startDate, endDate, pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Reservations retrieved", response));
    }

    @PutMapping("/update/{reservation_id}")
    @Operation(summary = "Update reservation")
    public ResponseEntity<ApiResponse<ReservationResponseDto>> updateReservation(
            @PathVariable("reservation_id") Integer reservationId,
            @Valid @RequestBody ReservationRequestDto dto) {
        ReservationResponseDto response = reservationService.updateReservation(reservationId, dto);
        return ResponseEntity.ok(ApiResponse.success("UPDATESUCCESS", "Reservation updated successfully", response));
    }

    @DeleteMapping("/{reservation_id}")
    @Operation(summary = "Delete a reservation")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(
            @PathVariable("reservation_id") Integer reservationId) {
        reservationService.deleteReservation(reservationId);
        return ResponseEntity.ok(ApiResponse.success("DELETESUCCESS", "Reservation deleted successfully", null));
    }
}
