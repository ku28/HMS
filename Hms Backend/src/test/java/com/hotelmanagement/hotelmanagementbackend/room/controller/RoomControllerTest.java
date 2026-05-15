package com.hotelmanagement.hotelmanagementbackend.room.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelmanagement.hotelmanagementbackend.config.TestSecurityConfig;
import com.hotelmanagement.hotelmanagementbackend.exception.GlobalExceptionHandler;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomResponseDto;
import com.hotelmanagement.hotelmanagementbackend.room.service.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.when;

@WebMvcTest(RoomController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
@DisplayName("RoomController Integration Tests")
class RoomControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private RoomService roomService;

    private RoomResponseDto buildResponseDto() {
        return RoomResponseDto.builder().roomId(1).roomNumber(101).isAvailable(true)
                .roomTypeId(1)
                .roomTypeName("Deluxe")
                .amenities(Collections.emptyList()).build();
    }

    @Test @DisplayName("shouldCreateRoomAndReturnCreated")
    void shouldCreateRoomAndReturnCreated() throws Exception {
        RoomRequestDto dto = RoomRequestDto.builder().roomNumber(102)
                .hotelId(1).roomTypeId(1).isAvailable(true).build();
        when(roomService.createRoom(any())).thenReturn(buildResponseDto());

        mockMvc.perform(post("/api/room-management/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("POSTSUCCESS"));
    }

    @Test @DisplayName("shouldReturnBadRequestForNegativeRoomNumber")
    void shouldReturnBadRequestForNegativeRoomNumber() throws Exception {
        RoomRequestDto dto = RoomRequestDto.builder().roomNumber(-1)
                .roomTypeId(1).isAvailable(true).build();

        mockMvc.perform(post("/api/room-management/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForMissingRoomTypeId")
    void shouldReturnBadRequestForMissingRoomTypeId() throws Exception {
        RoomRequestDto dto = RoomRequestDto.builder().roomNumber(101)
                .isAvailable(true).build();

        mockMvc.perform(post("/api/room-management/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldUpdateRoomSuccessfully")
    void shouldUpdateRoomSuccessfully() throws Exception {
        RoomRequestDto dto = RoomRequestDto.builder().roomNumber(101)
                .hotelId(1).roomTypeId(1).isAvailable(false).build();
        when(roomService.updateRoom(any(), any())).thenReturn(buildResponseDto());

        mockMvc.perform(put("/api/room-management/rooms/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("UPDATESUCCESS"));
    }

    @Test @DisplayName("shouldDeleteRoomSuccessfully")
    void shouldDeleteRoomSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/room-management/rooms/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DELETESUCCESS"));
    }
}
