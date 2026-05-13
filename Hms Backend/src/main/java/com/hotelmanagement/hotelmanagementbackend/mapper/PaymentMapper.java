package com.hotelmanagement.hotelmanagementbackend.mapper;

import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentRequestDto;
import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentResponseDto;
import com.hotelmanagement.hotelmanagementbackend.payment.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponseDto toResponseDto(Payment payment) {
        if (payment == null) return null;
        return PaymentResponseDto.builder()
                .paymentId(payment.getPaymentId())
                .reservationId(payment.getReservation() != null
                        ? payment.getReservation().getReservationId() : null)
                .amount(payment.getAmount())
                .paymentDate(payment.getPaymentDate())
                .paymentStatus(payment.getPaymentStatus())
                .paymentMethod(payment.getPaymentMethod())
                .build();
    }

    public Payment toEntity(PaymentRequestDto dto) {
        if (dto == null) return null;
        return Payment.builder()
                .amount(dto.getAmount())
                .paymentDate(dto.getPaymentDate())
                .paymentStatus(dto.getPaymentStatus())
                .paymentMethod(dto.getPaymentMethod())
                .build();
    }

    public void updateEntity(Payment payment, PaymentRequestDto dto) {
        if (dto.getAmount() != null) payment.setAmount(dto.getAmount());
        if (dto.getPaymentDate() != null) payment.setPaymentDate(dto.getPaymentDate());
        if (dto.getPaymentStatus() != null) payment.setPaymentStatus(dto.getPaymentStatus());
        if (dto.getPaymentMethod() != null) payment.setPaymentMethod(dto.getPaymentMethod());
    }
}
