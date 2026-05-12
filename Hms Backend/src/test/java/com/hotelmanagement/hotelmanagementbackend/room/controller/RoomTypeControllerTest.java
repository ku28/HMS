package com.hotelmanagement.hotelmanagementbackend.room.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.config.TestSecurityConfig;
import com.hotelmanagement.hotelmanagementbackend.exception.GlobalExceptionHandler;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomTypeRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomTypeResponseDto;
import com.hotelmanagement.hotelmanagementbackend.room.service.RoomTypeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoomTypeController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("RoomTypeController Integration Tests")
class RoomTypeControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private RoomTypeService roomTypeService;

    private RoomTypeResponseDto buildResponseDto() {
        return RoomTypeResponseDto.builder().roomTypeId(1).typeName("Deluxe Suite")
                .description("Luxury suite").maxOccupancy(2)
                .pricePerNight(new BigDecimal("350.00")).build();
    }

    @Test @DisplayName("shouldCreateRoomTypeAndReturnCreated")
    void shouldCreateRoomTypeAndReturnCreated() throws Exception {
        RoomTypeRequestDto dto = RoomTypeRequestDto.builder().typeName("Deluxe Suite")
                .description("Luxury suite").maxOccupancy(2)
                .pricePerNight(new BigDecimal("350.00")).build();
        when(roomTypeService.createRoomType(any())).thenReturn(buildResponseDto());

        mockMvc.perform(post("/api/RoomType/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("POSTSUCCESS"))
                .andExpect(jsonPath("$.data.typeName").value("Deluxe Suite"));
    }

    @Test @DisplayName("shouldReturnBadRequestForMissingTypeName")
    void shouldReturnBadRequestForMissingTypeName() throws Exception {
        RoomTypeRequestDto dto = RoomTypeRequestDto.builder()
                .maxOccupancy(2).pricePerNight(new BigDecimal("350.00")).build();

        mockMvc.perform(post("/api/RoomType/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForInvalidOccupancy")
    void shouldReturnBadRequestForInvalidOccupancy() throws Exception {
        RoomTypeRequestDto dto = RoomTypeRequestDto.builder().typeName("Standard")
                .maxOccupancy(-1).pricePerNight(new BigDecimal("100.00")).build();

        mockMvc.perform(post("/api/RoomType/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForNegativePrice")
    void shouldReturnBadRequestForNegativePrice() throws Exception {
        RoomTypeRequestDto dto = RoomTypeRequestDto.builder().typeName("Standard")
                .maxOccupancy(2).pricePerNight(new BigDecimal("-50.00")).build();

        mockMvc.perform(post("/api/RoomType/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnRoomTypeById")
    void shouldReturnRoomTypeById() throws Exception {
        when(roomTypeService.getRoomTypeById(1)).thenReturn(buildResponseDto());

        mockMvc.perform(get("/api/RoomType/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roomTypeId").value(1));
    }

    @Test @DisplayName("shouldReturnPaginatedRoomTypes")
    void shouldReturnPaginatedRoomTypes() throws Exception {
        PagedResponse<RoomTypeResponseDto> pagedResponse = PagedResponse.<RoomTypeResponseDto>builder()
                .content(List.of(buildResponseDto())).pageNumber(0).pageSize(20)
                .totalElements(1).totalPages(1).first(true).last(true).build();
        when(roomTypeService.getAllRoomTypes(any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/RoomType/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test @DisplayName("shouldDeleteRoomTypeSuccessfully")
    void shouldDeleteRoomTypeSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/RoomType/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DELETESUCCESS"));
    }
}
