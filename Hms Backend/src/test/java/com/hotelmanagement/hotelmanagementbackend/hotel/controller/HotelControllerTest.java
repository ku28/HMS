package com.hotelmanagement.hotelmanagementbackend.hotel.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.config.TestSecurityConfig;
import com.hotelmanagement.hotelmanagementbackend.exception.GlobalExceptionHandler;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.HotelRequestDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.HotelResponseDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.service.HotelService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HotelController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("HotelController Integration Tests")
class HotelControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private HotelService hotelService;

    private HotelResponseDto buildResponseDto() {
        return HotelResponseDto.builder().hotelId(1).name("Grand Plaza")
                .location("New York").description("Luxury hotel")
                .amenities(Collections.emptyList()).build();
    }

    @Test @DisplayName("shouldReturnHotelById")
    void shouldReturnHotelById() throws Exception {
        when(hotelService.getHotelById(1)).thenReturn(buildResponseDto());
        mockMvc.perform(get("/api/hotels/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Grand Plaza"));
    }

    @Test @DisplayName("shouldReturnPaginatedHotels")
    void shouldReturnPaginatedHotels() throws Exception {
        PagedResponse<HotelResponseDto> pagedResponse = PagedResponse.<HotelResponseDto>builder()
                .content(List.of(buildResponseDto())).pageNumber(0).pageSize(10)
                .totalElements(1).totalPages(1).first(true).last(true).build();
        when(hotelService.searchHotels(any(), any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/hotels/all").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test @DisplayName("shouldCreateHotelAndReturnCreated")
    void shouldCreateHotelAndReturnCreated() throws Exception {
        HotelRequestDto dto = HotelRequestDto.builder().name("Grand Plaza")
                .location("New York").description("Luxury hotel").build();
        when(hotelService.createHotel(any())).thenReturn(buildResponseDto());

        mockMvc.perform(post("/api/hotels/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("POSTSUCCESS"));
    }

    @Test @DisplayName("shouldReturnBadRequestForBlankHotelName")
    void shouldReturnBadRequestForBlankHotelName() throws Exception {
        HotelRequestDto dto = HotelRequestDto.builder().name("")
                .location("New York").build();

        mockMvc.perform(post("/api/hotels/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForBlankLocation")
    void shouldReturnBadRequestForBlankLocation() throws Exception {
        HotelRequestDto dto = HotelRequestDto.builder().name("Hotel")
                .location("").build();

        mockMvc.perform(post("/api/hotels/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldDeleteHotelSuccessfully")
    void shouldDeleteHotelSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/hotels/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DELETESUCCESS"));
    }

    @Test @DisplayName("shouldReturnEmptyPageWhenNoHotelsFound")
    void shouldReturnEmptyPageWhenNoHotelsFound() throws Exception {
        PagedResponse<HotelResponseDto> emptyResponse = PagedResponse.<HotelResponseDto>builder()
                .content(Collections.emptyList()).pageNumber(0).pageSize(10)
                .totalElements(0).totalPages(0).first(true).last(true).build();
        when(hotelService.searchHotels(any(), any())).thenReturn(emptyResponse);

        mockMvc.perform(get("/api/hotels/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isEmpty())
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }
}
