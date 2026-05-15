package com.hotelmanagement.hotelmanagementbackend.reservation.repository;

import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RepositoryRestResource(exported = false)
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    Page<Reservation> findByGuestEmailIgnoreCase(String guestEmail, Pageable pageable);

    Page<Reservation> findByCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual(
            LocalDate startDate, LocalDate endDate, Pageable pageable);

    Page<Reservation> findByRoom_RoomId(Integer roomId, Pageable pageable);

    Page<Reservation> findByReservationIdGreaterThan(Integer reservationId, Pageable pageable);

    boolean existsByRoom_RoomIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
            Integer roomId, LocalDate checkOutDate, LocalDate checkInDate);

    long count();
}
