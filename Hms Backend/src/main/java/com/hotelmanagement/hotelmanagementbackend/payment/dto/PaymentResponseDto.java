package com.hotelmanagement.hotelmanagementbackend.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDto implements Serializable {

    private Integer paymentId;
    private Integer reservationId;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private String paymentStatus;
    private String paymentMethod;
}
