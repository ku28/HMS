package com.hotelmanagement.hotelmanagementbackend.review.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.config.TestSecurityConfig;
import com.hotelmanagement.hotelmanagementbackend.exception.GlobalExceptionHandler;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewRequestDto;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewResponseDto;
import com.hotelmanagement.hotelmanagementbackend.review.service.ReviewService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("ReviewController Integration Tests")
class ReviewControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private ReviewService reviewService;

    private ReviewResponseDto buildResponseDto() {
        return ReviewResponseDto.builder().reviewId(1).reservationId(1)
                .rating(5).comment("Excellent stay!").reviewDate(LocalDate.now()).build();
    }

    @Test @DisplayName("shouldCreateReviewAndReturnCreated")
    void shouldCreateReviewAndReturnCreated() throws Exception {
        ReviewRequestDto dto = ReviewRequestDto.builder().reservationId(1)
                .rating(5).comment("Excellent stay!").reviewDate(LocalDate.now()).build();
        when(reviewService.createReview(any())).thenReturn(buildResponseDto());

        mockMvc.perform(post("/api/review/post")
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

        mockMvc.perform(post("/api/review/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForRatingBelowMin")
    void shouldReturnBadRequestForRatingBelowMin() throws Exception {
        ReviewRequestDto dto = ReviewRequestDto.builder().reservationId(1)
                .rating(0).comment("Too low").reviewDate(LocalDate.now()).build();

        mockMvc.perform(post("/api/review/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForMissingReservationId")
    void shouldReturnBadRequestForMissingReservationId() throws Exception {
        ReviewRequestDto dto = ReviewRequestDto.builder()
                .rating(5).comment("Good").reviewDate(LocalDate.now()).build();

        mockMvc.perform(post("/api/review/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnReviewById")
    void shouldReturnReviewById() throws Exception {
        when(reviewService.getReviewById(1)).thenReturn(buildResponseDto());

        mockMvc.perform(get("/api/review/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reviewId").value(1));
    }

    @Test @DisplayName("shouldReturnPaginatedReviews")
    void shouldReturnPaginatedReviews() throws Exception {
        PagedResponse<ReviewResponseDto> pagedResponse = PagedResponse.<ReviewResponseDto>builder()
                .content(List.of(buildResponseDto())).pageNumber(0).pageSize(10)
                .totalElements(1).totalPages(1).first(true).last(true).build();
        when(reviewService.getAllReviews(any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/review/all").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test @DisplayName("shouldReturnReviewsByRating")
    void shouldReturnReviewsByRating() throws Exception {
        PagedResponse<ReviewResponseDto> pagedResponse = PagedResponse.<ReviewResponseDto>builder()
                .content(List.of(buildResponseDto())).pageNumber(0).pageSize(10)
                .totalElements(1).totalPages(1).first(true).last(true).build();
        when(reviewService.getReviewsByRating(eq(5), any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/reviews/rating/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].rating").value(5));
    }

    @Test @DisplayName("shouldReturnMostRecentReview")
    void shouldReturnMostRecentReview() throws Exception {
        when(reviewService.getMostRecentReview()).thenReturn(buildResponseDto());

        mockMvc.perform(get("/api/reviews/recent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.reviewId").value(1));
    }

    @Test @DisplayName("shouldDeleteReviewSuccessfully")
    void shouldDeleteReviewSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/review/delete/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DELETESUCCESS"));
    }
}
