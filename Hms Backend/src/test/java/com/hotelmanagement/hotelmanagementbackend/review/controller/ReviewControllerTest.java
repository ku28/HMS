package com.hotelmanagement.hotelmanagementbackend.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelmanagement.hotelmanagementbackend.config.TestSecurityConfig;
import com.hotelmanagement.hotelmanagementbackend.exception.GlobalExceptionHandler;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewRequestDto;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewResponseDto;
import com.hotelmanagement.hotelmanagementbackend.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
@DisplayName("ReviewController Integration Tests")
class ReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private ReviewService reviewService;

    private ReviewResponseDto buildResponseDto() {
        return ReviewResponseDto.builder().reviewId(1).reservationId(1)
                .rating(5).comment("Excellent stay!").reviewDate(LocalDate.now()).build();
    }

    @Test @DisplayName("shouldCreateReviewAndReturnCreated")
    void shouldCreateReviewAndReturnCreated() throws Exception {
        ReviewRequestDto dto = ReviewRequestDto.builder().reservationId(1)
                .rating(5).comment("Excellent stay!").reviewDate(LocalDate.now()).build();
        when(reviewService.createReview(any())).thenReturn(buildResponseDto());

        mockMvc.perform(post("/api/review-management/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("POSTSUCCESS"))
                .andExpect(jsonPath("$.data.rating").value(5));
    }

    @Test @DisplayName("shouldReturnBadRequestForRatingAboveMax")
    void shouldReturnBadRequestForRatingAboveMax() throws Exception {
        ReviewRequestDto dto = ReviewRequestDto.builder().reservationId(1)
                .rating(6).comment("Too high").reviewDate(LocalDate.now()).build();

        mockMvc.perform(post("/api/review-management/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForRatingBelowMin")
    void shouldReturnBadRequestForRatingBelowMin() throws Exception {
        ReviewRequestDto dto = ReviewRequestDto.builder().reservationId(1)
                .rating(0).comment("Too low").reviewDate(LocalDate.now()).build();

        mockMvc.perform(post("/api/review-management/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForMissingReservationId")
    void shouldReturnBadRequestForMissingReservationId() throws Exception {
        ReviewRequestDto dto = ReviewRequestDto.builder()
                .rating(5).comment("Good").reviewDate(LocalDate.now()).build();

        mockMvc.perform(post("/api/review-management/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldDeleteReviewSuccessfully")
    void shouldDeleteReviewSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/review-management/reviews/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DELETESUCCESS"));
    }
}
