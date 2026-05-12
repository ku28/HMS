package com.hotelmanagement.hotelmanagementbackend.payment.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponseMapper;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.mapper.PaymentMapper;
import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentRequestDto;
import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentResponseDto;
import com.hotelmanagement.hotelmanagementbackend.payment.entity.Payment;
import com.hotelmanagement.hotelmanagementbackend.payment.repository.PaymentRepository;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.reservation.repository.ReservationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;
    private final PaymentMapper paymentMapper;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              ReservationRepository reservationRepository,
                              PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.reservationRepository = reservationRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto dto) {
        if (paymentRepository.existsByReservation_ReservationId(dto.getReservationId())) {
            throw new ResourceAlreadyExistsException("Payment", "reservationId", dto.getReservationId());
        }
        Reservation reservation = reservationRepository.findById(dto.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reservation", "reservationId", dto.getReservationId()));
        Payment payment = paymentMapper.toEntity(dto);
        payment.setReservation(reservation);
        Payment saved = paymentRepository.save(payment);
        return paymentMapper.toResponseDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDto getPaymentById(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", "paymentId", paymentId));
        return paymentMapper.toResponseDto(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PaymentResponseDto> getAllPayments(Pageable pageable) {
        Page<Payment> page = paymentRepository.findByPaymentStatusIgnoreCase("Paid", pageable);
        List<PaymentResponseDto> dtos = page.getContent().stream()
                .map(paymentMapper::toResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<PaymentResponseDto> getPaymentsByStatus(String status, Pageable pageable) {
        Page<Payment> page = paymentRepository.findByPaymentStatusIgnoreCase(status, pageable);
        List<PaymentResponseDto> dtos = page.getContent().stream()
                .map(paymentMapper::toResponseDto)
                .collect(Collectors.toList());
        return PagedResponseMapper.toPagedResponse(page, dtos);
    }

    @Override
    public void deletePayment(Integer paymentId) {
        if (!paymentRepository.existsById(paymentId)) {
            throw new ResourceNotFoundException("Payment", "paymentId", paymentId);
        }
        paymentRepository.deleteById(paymentId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getTotalRevenue() {
        Page<Payment> allPaid = paymentRepository.findByPaymentStatusIgnoreCase("Paid", Pageable.unpaged());
        return allPaid.getContent().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
