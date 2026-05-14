package com.hotelmanagement.hotelmanagementbackend.payment.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.payment.entity.Payment;
import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryDataJpaTest
@DisplayName("PaymentRepository Tests")
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByPaymentStatusIgnoreCase should match payment status regardless of case")
    void findByPaymentStatusIgnoreCaseShouldMatchPaymentStatusRegardlessOfCase() {
        Reservation firstReservation = persistReservation(101, "paid-one@example.com");
        Reservation secondReservation = persistReservation(102, "pending@example.com");
        persistPayment(firstReservation, "Paid", new BigDecimal("250.00"));
        persistPayment(secondReservation, "Pending", new BigDecimal("150.00"));

        Page<Payment> result = paymentRepository.findByPaymentStatusIgnoreCase("paid", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Payment::getPaymentStatus)
                .containsExactly("Paid");
    }

    @Test
    @DisplayName("findByReservation_ReservationId should return payments for reservation")
    void findByReservationReservationIdShouldReturnPaymentsForReservation() {
        Reservation firstReservation = persistReservation(201, "first@example.com");
        Reservation secondReservation = persistReservation(202, "second@example.com");
        persistPayment(firstReservation, "Paid", new BigDecimal("300.00"));
        persistPayment(secondReservation, "Failed", new BigDecimal("200.00"));

        Page<Payment> result = paymentRepository.findByReservation_ReservationId(
                firstReservation.getReservationId(), PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Payment::getAmount)
                .containsExactly(new BigDecimal("300.00"));
    }

    @Test
    @DisplayName("existsByReservation_ReservationId should report whether reservation has payment")
    void existsByReservationReservationIdShouldReportWhetherReservationHasPayment() {
        Reservation paidReservation = persistReservation(301, "paid@example.com");
        Reservation unpaidReservation = persistReservation(302, "unpaid@example.com");
        persistPayment(paidReservation, "Paid", new BigDecimal("450.00"));

        assertThat(paymentRepository.existsByReservation_ReservationId(paidReservation.getReservationId())).isTrue();
        assertThat(paymentRepository.existsByReservation_ReservationId(unpaidReservation.getReservationId())).isFalse();
    }

    private Payment persistPayment(Reservation reservation, String status, BigDecimal amount) {
        return entityManager.persistAndFlush(Payment.builder()
                .reservation(reservation)
                .amount(amount)
                .paymentDate(LocalDate.of(2026, 11, 1))
                .paymentStatus(status)
                .paymentMethod("CARD")
                .build());
    }

    private Reservation persistReservation(Integer roomNumber, String guestEmail) {
        Hotel hotel = entityManager.persistAndFlush(Hotel.builder()
                .name("Payment Hotel " + roomNumber)
                .location("Mumbai")
                .description("Payment test hotel")
                .build());
        RoomType roomType = entityManager.persistAndFlush(RoomType.builder()
                .typeName("Payment Type " + roomNumber)
                .description("Payment test type")
                .maxOccupancy(2)
                .pricePerNight(new BigDecimal("250.00"))
                .build());
        Room room = entityManager.persistAndFlush(Room.builder()
                .roomNumber(roomNumber)
                .hotel(hotel)
                .roomType(roomType)
                .isAvailable(true)
                .build());
        return entityManager.persistAndFlush(Reservation.builder()
                .guestName("Payment Guest")
                .guestEmail(guestEmail)
                .guestPhone("9999999999")
                .room(room)
                .checkInDate(LocalDate.of(2026, 11, 1))
                .checkOutDate(LocalDate.of(2026, 11, 3))
                .build());
    }
}
