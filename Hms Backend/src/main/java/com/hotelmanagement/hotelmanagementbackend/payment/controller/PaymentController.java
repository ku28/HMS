package com.hotelmanagement.hotelmanagementbackend.payment.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentRequestDto;
import com.hotelmanagement.hotelmanagementbackend.payment.dto.PaymentResponseDto;
import com.hotelmanagement.hotelmanagementbackend.payment.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@Tag(name = "Payment", description = "Payment Management APIs")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/payment/post")
    @Operation(summary = "Create a payment")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> createPayment(
            @Valid @RequestBody PaymentRequestDto dto) {
        PaymentResponseDto response = paymentService.createPayment(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("POSTSUCCESS", "Payment added successfully", response));
    }

    @GetMapping("/api/payment/{payment_id}")
    @Operation(summary = "Get payment by ID")
    public ResponseEntity<ApiResponse<PaymentResponseDto>> getPaymentById(
            @PathVariable("payment_id") Integer paymentId) {
        PaymentResponseDto response = paymentService.getPaymentById(paymentId);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Payment retrieved", response));
    }

    @GetMapping("/api/payment/all")
    @Operation(summary = "Get all payments")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponseDto>>> getAllPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<PaymentResponseDto> response = paymentService.getAllPayments(pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Payments retrieved", response));
    }

    @GetMapping("/api/payments/status/{status}")
    @Operation(summary = "Get payments by status")
    public ResponseEntity<ApiResponse<PagedResponse<PaymentResponseDto>>> getPaymentsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<PaymentResponseDto> response = paymentService.getPaymentsByStatus(status, pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Payments retrieved", response));
    }

    @GetMapping("/api/payments/total-revenue")
    @Operation(summary = "Get total revenue")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalRevenue() {
        BigDecimal revenue = paymentService.getTotalRevenue();
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Total revenue", revenue));
    }

    @DeleteMapping("/api/payment/{payment_id}")
    @Operation(summary = "Delete a payment")
    public ResponseEntity<ApiResponse<Void>> deletePayment(
            @PathVariable("payment_id") Integer paymentId) {
        paymentService.deletePayment(paymentId);
        return ResponseEntity.ok(ApiResponse.success("DELETESUCCESS", "Payment deleted successfully", null));
    }
}
