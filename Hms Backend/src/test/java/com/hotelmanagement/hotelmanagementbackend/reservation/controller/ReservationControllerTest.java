package com.hotelmanagement.hotelmanagementbackend.reservation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.config.TestSecurityConfig;
import com.hotelmanagement.hotelmanagementbackend.exception.GlobalExceptionHandler;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationRequestDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationResponseDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.service.ReservationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("ReservationController Integration Tests")
class ReservationControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private ReservationService reservationService;

    private ReservationResponseDto buildResponseDto() {
        return ReservationResponseDto.builder().reservationId(1).guestName("John Doe")
                .guestEmail("john@example.com").guestPhone("+1234567890")
                .checkInDate(LocalDate.of(2026, 6, 1)).checkOutDate(LocalDate.of(2026, 6, 5))
                .roomId(1).roomNumber(101).roomTypeName("Deluxe").build();
    }

    @Test @DisplayName("shouldCreateReservationAndReturnCreated")
    void shouldCreateReservationAndReturnCreated() throws Exception {
        ReservationRequestDto dto = ReservationRequestDto.builder().guestName("John Doe")
                .guestEmail("john@example.com").guestPhone("+1234567890")
                .checkInDate(LocalDate.of(2026, 6, 1)).checkOutDate(LocalDate.of(2026, 6, 5))
                .roomId(1).build();
        when(reservationService.createReservation(any())).thenReturn(buildResponseDto());

        mockMvc.perform(post("/api/reservation/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("POSTSUCCESS"))
                .andExpect(jsonPath("$.data.guestName").value("John Doe"));
    }

    @Test @DisplayName("shouldReturnReservationById")
    void shouldReturnReservationById() throws Exception {
        when(reservationService.getReservationById(1)).thenReturn(buildResponseDto());

        mockMvc.perform(get("/api/reservation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reservationId").value(1));
    }

    @Test @DisplayName("shouldReturnPaginatedReservations")
    void shouldReturnPaginatedReservations() throws Exception {
        PagedResponse<ReservationResponseDto> pagedResponse = PagedResponse.<ReservationResponseDto>builder()
                .content(List.of(buildResponseDto())).pageNumber(0).pageSize(10)
                .totalElements(1).totalPages(1).first(true).last(true).build();
        when(reservationService.getAllReservations(any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/reservation/all")
                        .param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test @DisplayName("shouldReturnBadRequestForMissingGuestName")
    void shouldReturnBadRequestForMissingGuestName() throws Exception {
        ReservationRequestDto dto = ReservationRequestDto.builder()
                .guestEmail("john@example.com").guestPhone("+1234567890")
                .checkInDate(LocalDate.of(2026, 6, 1)).checkOutDate(LocalDate.of(2026, 6, 5))
                .roomId(1).build();

        mockMvc.perform(post("/api/reservation/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForInvalidGuestEmail")
    void shouldReturnBadRequestForInvalidGuestEmail() throws Exception {
        ReservationRequestDto dto = ReservationRequestDto.builder().guestName("John")
                .guestEmail("not-valid").guestPhone("+1234567890")
                .checkInDate(LocalDate.of(2026, 6, 1)).checkOutDate(LocalDate.of(2026, 6, 5))
                .roomId(1).build();

        mockMvc.perform(post("/api/reservation/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForMissingRoomId")
    void shouldReturnBadRequestForMissingRoomId() throws Exception {
        ReservationRequestDto dto = ReservationRequestDto.builder().guestName("John")
                .guestEmail("john@example.com").guestPhone("+1234567890")
                .checkInDate(LocalDate.of(2026, 6, 1)).checkOutDate(LocalDate.of(2026, 6, 5))
                .build();

        mockMvc.perform(post("/api/reservation/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldDeleteReservationSuccessfully")
    void shouldDeleteReservationSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/reservation/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DELETESUCCESS"));
    }
}
