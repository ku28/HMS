package com.hotelmanagement.hotelmanagementbackend.admin.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.payment.service.PaymentService;
import com.hotelmanagement.hotelmanagementbackend.reservation.dto.ReservationResponseDto;
import com.hotelmanagement.hotelmanagementbackend.reservation.service.ReservationService;
import com.hotelmanagement.hotelmanagementbackend.review.dto.ReviewResponseDto;
import com.hotelmanagement.hotelmanagementbackend.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Admin", description = "Admin Dashboard APIs")
public class AdminController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final ReviewService reviewService;

    public AdminController(ReservationService reservationService,
                           PaymentService paymentService,
                           ReviewService reviewService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.reviewService = reviewService;
    }

    @GetMapping("/dashboard")
    @Operation(summary = "Get admin dashboard stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalReservations = reservationService.getTotalReservations();
        BigDecimal totalRevenue = paymentService.getTotalRevenue();

        PagedResponse<ReservationResponseDto> recentBookings = reservationService
                .getAllReservations(PageRequest.of(0, 10, Sort.by("reservationId").descending()));

        PagedResponse<ReviewResponseDto> recentReviews = reviewService
                .getAllReviews(PageRequest.of(0, 10));

        stats.put("totalReservations", totalReservations);
        stats.put("totalRevenue", totalRevenue);
        stats.put("recentBookings", recentBookings.getContent());
        stats.put("recentReviews", recentReviews.getContent());

        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Dashboard stats retrieved", stats));
    }
}
