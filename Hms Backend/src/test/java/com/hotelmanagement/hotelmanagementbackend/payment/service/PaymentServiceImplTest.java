package com.hotelmanagement.hotelmanagementbackend.payment.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.mapper.PaymentMapper;
import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentRequestDto;
import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentResponseDto;
import com.hotelmanagement.hotelmanagementbackend.payment.entity.Payment;
import com.hotelmanagement.hotelmanagementbackend.payment.repository.PaymentRepository;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentService Unit Tests")
class PaymentServiceImplTest {

    @Mock private PaymentRepository paymentRepository;
    @Mock private ReservationRepository reservationRepository;
    @Mock private PaymentMapper paymentMapper;
    @InjectMocks private PaymentServiceImpl paymentService;

    private Payment testPayment;
    private PaymentRequestDto testRequestDto;
    private PaymentResponseDto testResponseDto;
    private Reservation testReservation;

    @BeforeEach
    void setUp() {
        testReservation = Reservation.builder().reservationId(1).guestName("John Doe").build();
        testPayment = Payment.builder().paymentId(1).reservation(testReservation)
                .amount(new BigDecimal("250.00")).paymentDate(LocalDate.now())
                .paymentStatus("Paid").build();
        testRequestDto = PaymentRequestDto.builder().reservationId(1)
                .amount(new BigDecimal("250.00")).paymentDate(LocalDate.now())
                .paymentStatus("Paid").build();
        testResponseDto = PaymentResponseDto.builder().paymentId(1).reservationId(1)
                .amount(new BigDecimal("250.00")).paymentDate(LocalDate.now())
                .paymentStatus("Paid").build();
    }

    @Test @DisplayName("shouldCreatePaymentSuccessfully")
    void shouldCreatePaymentSuccessfully() {
        when(paymentRepository.existsByReservation_ReservationId(1)).thenReturn(false);
        when(reservationRepository.findById(1)).thenReturn(Optional.of(testReservation));
        when(paymentMapper.toEntity(testRequestDto)).thenReturn(testPayment);
        when(paymentRepository.save(any())).thenReturn(testPayment);
        when(paymentMapper.toResponseDto(testPayment)).thenReturn(testResponseDto);

        PaymentResponseDto result = paymentService.createPayment(testRequestDto);
        assertThat(result.getPaymentId()).isEqualTo(1);
        assertThat(result.getAmount()).isEqualByComparingTo(new BigDecimal("250.00"));
    }

    @Test @DisplayName("shouldThrowExceptionWhenPaymentAlreadyExistsForReservation")
    void shouldThrowExceptionWhenPaymentAlreadyExistsForReservation() {
        when(paymentRepository.existsByReservation_ReservationId(1)).thenReturn(true);
        assertThatThrownBy(() -> paymentService.createPayment(testRequestDto))
                .isInstanceOf(ResourceAlreadyExistsException.class);
        verify(paymentRepository, never()).save(any());
    }

    @Test @DisplayName("shouldThrowExceptionWhenReservationNotFoundForPayment")
    void shouldThrowExceptionWhenReservationNotFoundForPayment() {
        PaymentRequestDto dto = PaymentRequestDto.builder().reservationId(999)
                .amount(new BigDecimal("100.00")).paymentDate(LocalDate.now())
                .paymentStatus("Paid").build();
        when(paymentRepository.existsByReservation_ReservationId(999)).thenReturn(false);
        when(reservationRepository.findById(999)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> paymentService.createPayment(dto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("shouldReturnPaymentById")
    void shouldReturnPaymentById() {
        when(paymentRepository.findById(1)).thenReturn(Optional.of(testPayment));
        when(paymentMapper.toResponseDto(testPayment)).thenReturn(testResponseDto);
        PaymentResponseDto result = paymentService.getPaymentById(1);
        assertThat(result.getPaymentStatus()).isEqualTo("Paid");
    }

    @Test @DisplayName("shouldThrowExceptionWhenPaymentNotFound")
    void shouldThrowExceptionWhenPaymentNotFound() {
        when(paymentRepository.findById(999)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> paymentService.getPaymentById(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("shouldReturnPaginatedPaymentsByStatus")
    void shouldReturnPaginatedPaymentsByStatus() {
        Pageable p = PageRequest.of(0, 10);
        Page<Payment> page = new PageImpl<>(List.of(testPayment), p, 1);
        when(paymentRepository.findByPaymentStatusIgnoreCase("Paid", p)).thenReturn(page);
        when(paymentMapper.toResponseDto(testPayment)).thenReturn(testResponseDto);
        PagedResponse<PaymentResponseDto> result = paymentService.getPaymentsByStatus("Paid", p);
        assertThat(result.getContent()).hasSize(1);
    }

    @Test @DisplayName("shouldDeletePaymentSuccessfully")
    void shouldDeletePaymentSuccessfully() {
        when(paymentRepository.existsById(1)).thenReturn(true);
        paymentService.deletePayment(1);
        verify(paymentRepository).deleteById(1);
    }

    @Test @DisplayName("shouldThrowExceptionWhenDeletingNonExistentPayment")
    void shouldThrowExceptionWhenDeletingNonExistentPayment() {
        when(paymentRepository.existsById(999)).thenReturn(false);
        assertThatThrownBy(() -> paymentService.deletePayment(999))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test @DisplayName("shouldCalculateTotalRevenueCorrectly")
    void shouldCalculateTotalRevenueCorrectly() {
        Payment p1 = Payment.builder().amount(new BigDecimal("250.00")).paymentStatus("Paid").build();
        Payment p2 = Payment.builder().amount(new BigDecimal("350.00")).paymentStatus("Paid").build();
        Page<Payment> page = new PageImpl<>(List.of(p1, p2));
        when(paymentRepository.findByPaymentStatusIgnoreCase("Paid", Pageable.unpaged())).thenReturn(page);
        BigDecimal revenue = paymentService.getTotalRevenue();
        assertThat(revenue).isEqualByComparingTo(new BigDecimal("600.00"));
    }
}
