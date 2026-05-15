package com.hotelmanagement.hotelmanagementbackend.payment.repository;

import com.hotelmanagement.hotelmanagementbackend.payment.entity.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(exported = false)
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Page<Payment> findByPaymentStatusIgnoreCase(String paymentStatus, Pageable pageable);

    Page<Payment> findByReservation_ReservationId(Integer reservationId, Pageable pageable);

    boolean existsByReservation_ReservationId(Integer reservationId);
}
