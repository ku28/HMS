package com.hotelmanagement.hotelmanagementbackend.room.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.config.TestSecurityConfig;
import com.hotelmanagement.hotelmanagementbackend.exception.GlobalExceptionHandler;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomResponseDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomTypeResponseDto;
import com.hotelmanagement.hotelmanagementbackend.room.service.RoomService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("RoomController Integration Tests")
class RoomControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private RoomService roomService;

    private RoomResponseDto buildResponseDto() {
        return RoomResponseDto.builder().roomId(1).roomNumber(101).isAvailable(true)
                .roomType(RoomTypeResponseDto.builder().roomTypeId(1).typeName("Deluxe")
                        .maxOccupancy(2).pricePerNight(new BigDecimal("200.00")).build())
                .amenities(Collections.emptyList()).build();
    }

    @Test @DisplayName("shouldReturnRoomById")
    void shouldReturnRoomById() throws Exception {
        when(roomService.getRoomById(1)).thenReturn(buildResponseDto());
        mockMvc.perform(get("/api/room/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roomNumber").value(101));
    }

    @Test @DisplayName("shouldReturnPaginatedRooms")
    void shouldReturnPaginatedRooms() throws Exception {
        PagedResponse<RoomResponseDto> pagedResponse = PagedResponse.<RoomResponseDto>builder()
                .content(List.of(buildResponseDto())).pageNumber(0).pageSize(10)
                .totalElements(1).totalPages(1).first(true).last(true).build();
        when(roomService.getAllRooms(any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/room/all").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test @DisplayName("shouldCreateRoomAndReturnCreated")
    void shouldCreateRoomAndReturnCreated() throws Exception {
        RoomRequestDto dto = RoomRequestDto.builder().roomNumber(102)
                .roomTypeId(1).isAvailable(true).build();
        when(roomService.createRoom(any())).thenReturn(buildResponseDto());

        mockMvc.perform(post("/api/rooms/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("POSTSUCCESS"));
    }

    @Test @DisplayName("shouldReturnBadRequestForNegativeRoomNumber")
    void shouldReturnBadRequestForNegativeRoomNumber() throws Exception {
        RoomRequestDto dto = RoomRequestDto.builder().roomNumber(-1)
                .roomTypeId(1).isAvailable(true).build();

        mockMvc.perform(post("/api/rooms/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForMissingRoomTypeId")
    void shouldReturnBadRequestForMissingRoomTypeId() throws Exception {
        RoomRequestDto dto = RoomRequestDto.builder().roomNumber(101)
                .isAvailable(true).build();

        mockMvc.perform(post("/api/rooms/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldDeleteRoomSuccessfully")
    void shouldDeleteRoomSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/room/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DELETESUCCESS"));
    }
}
