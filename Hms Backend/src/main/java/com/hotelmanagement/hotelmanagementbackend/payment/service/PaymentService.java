package com.hotelmanagement.hotelmanagementbackend.payment.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentRequestDto;
import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentResponseDto;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentRequestDto dto);

    PaymentResponseDto getPaymentById(Integer paymentId);

    PagedResponse<PaymentResponseDto> getAllPayments(Pageable pageable);

    PagedResponse<PaymentResponseDto> getPaymentsByStatus(String status, Pageable pageable);

    void deletePayment(Integer paymentId);

    BigDecimal getTotalRevenue();
}
