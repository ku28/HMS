package com.hotelmanagement.hotelmanagementbackend.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.config.TestSecurityConfig;
import com.hotelmanagement.hotelmanagementbackend.exception.GlobalExceptionHandler;
import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentRequestDto;
import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentResponseDto;
import com.hotelmanagement.hotelmanagementbackend.payment.service.PaymentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.bean.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@DisplayName("PaymentController Integration Tests")
class PaymentControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockBean private PaymentService paymentService;

    private PaymentResponseDto buildResponseDto() {
        return PaymentResponseDto.builder().paymentId(1).reservationId(1)
                .amount(new BigDecimal("250.00")).paymentDate(LocalDate.now())
                .paymentStatus("Paid").build();
    }

    @Test @DisplayName("shouldCreatePaymentAndReturnCreated")
    void shouldCreatePaymentAndReturnCreated() throws Exception {
        PaymentRequestDto dto = PaymentRequestDto.builder().reservationId(1)
                .amount(new BigDecimal("250.00")).paymentDate(LocalDate.now())
                .paymentStatus("Paid").build();
        when(paymentService.createPayment(any())).thenReturn(buildResponseDto());

        mockMvc.perform(post("/api/payment/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("POSTSUCCESS"))
                .andExpect(jsonPath("$.data.amount").value(250.00));
    }

    @Test @DisplayName("shouldReturnBadRequestForNegativePaymentAmount")
    void shouldReturnBadRequestForNegativePaymentAmount() throws Exception {
        PaymentRequestDto dto = PaymentRequestDto.builder().reservationId(1)
                .amount(new BigDecimal("-50.00")).paymentDate(LocalDate.now())
                .paymentStatus("Paid").build();

        mockMvc.perform(post("/api/payment/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForMissingAmount")
    void shouldReturnBadRequestForMissingAmount() throws Exception {
        PaymentRequestDto dto = PaymentRequestDto.builder().reservationId(1)
                .paymentDate(LocalDate.now()).paymentStatus("Paid").build();

        mockMvc.perform(post("/api/payment/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnPaymentById")
    void shouldReturnPaymentById() throws Exception {
        when(paymentService.getPaymentById(1)).thenReturn(buildResponseDto());

        mockMvc.perform(get("/api/payment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentId").value(1));
    }

    @Test @DisplayName("shouldReturnPaginatedPayments")
    void shouldReturnPaginatedPayments() throws Exception {
        PagedResponse<PaymentResponseDto> pagedResponse = PagedResponse.<PaymentResponseDto>builder()
                .content(List.of(buildResponseDto())).pageNumber(0).pageSize(10)
                .totalElements(1).totalPages(1).first(true).last(true).build();
        when(paymentService.getAllPayments(any())).thenReturn(pagedResponse);

        mockMvc.perform(get("/api/payment/all").param("page", "0").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(1));
    }

    @Test @DisplayName("shouldReturnTotalRevenue")
    void shouldReturnTotalRevenue() throws Exception {
        when(paymentService.getTotalRevenue()).thenReturn(new BigDecimal("5000.00"));

        mockMvc.perform(get("/api/payments/total-revenue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(5000.00));
    }

    @Test @DisplayName("shouldDeletePaymentSuccessfully")
    void shouldDeletePaymentSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/payment/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("DELETESUCCESS"));
    }
}
