package com.hotelmanagement.hotelmanagementbackend.payment.repository;

import com.hotelmanagement.hotelmanagementbackend.payment.entity.Payment;
import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryDataJpaTest
class PaymentRepositoryTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void cleanDatabase() {
        paymentRepository.deleteAll();
    }

    @Test
    void testSaveAndFindById() {

        Reservation reservation = reservationRepository.save(

                Reservation.builder()
                        .guestName("Anshul")
                        .guestEmail("anshul@gmail.com")
                        .guestPhone("9999999999")
                        .checkInDate(LocalDate.now())
                        .checkOutDate(LocalDate.now().plusDays(2))
                        .build()
        );

        Payment payment = Payment.builder()
                .amount(BigDecimal.valueOf(150.00))
                .paymentDate(LocalDate.now())
                .paymentStatus("COMPLETED")
                .paymentMethod("CREDIT_CARD")
                .reservation(reservation)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        assertThat(savedPayment.getPaymentId()).isNotNull();

        Optional<Payment> foundPayment =
                paymentRepository.findById(savedPayment.getPaymentId());

        assertThat(foundPayment).isPresent();

        assertThat(foundPayment.get().getAmount())
                .isEqualByComparingTo(BigDecimal.valueOf(150.00));

        assertThat(foundPayment.get().getPaymentStatus())
                .isEqualTo("COMPLETED");
    }

    @Test
    void testFindByPaymentStatusIgnoreCase() {

        Reservation reservation1 = reservationRepository.save(

                Reservation.builder()
                        .guestName("User1")
                        .guestEmail("user1@gmail.com")
                        .guestPhone("1111111111")
                        .checkInDate(LocalDate.now())
                        .checkOutDate(LocalDate.now().plusDays(1))
                        .build()
        );

        Reservation reservation2 = reservationRepository.save(

                Reservation.builder()
                        .guestName("User2")
                        .guestEmail("user2@gmail.com")
                        .guestPhone("2222222222")
                        .checkInDate(LocalDate.now())
                        .checkOutDate(LocalDate.now().plusDays(1))
                        .build()
        );

        Reservation reservation3 = reservationRepository.save(

                Reservation.builder()
                        .guestName("User3")
                        .guestEmail("user3@gmail.com")
                        .guestPhone("3333333333")
                        .checkInDate(LocalDate.now())
                        .checkOutDate(LocalDate.now().plusDays(1))
                        .build()
        );

        paymentRepository.saveAll(List.of(

                Payment.builder()
                        .amount(BigDecimal.valueOf(100))
                        .paymentDate(LocalDate.now())
                        .paymentStatus("PAID")
                        .paymentMethod("CARD")
                        .reservation(reservation1)
                        .build(),

                Payment.builder()
                        .amount(BigDecimal.valueOf(200))
                        .paymentDate(LocalDate.now())
                        .paymentStatus("pending")
                        .paymentMethod("CASH")
                        .reservation(reservation2)
                        .build(),

                Payment.builder()
                        .amount(BigDecimal.valueOf(300))
                        .paymentDate(LocalDate.now())
                        .paymentStatus("PAID")
                        .paymentMethod("UPI")
                        .reservation(reservation3)
                        .build()
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Payment> result =
                paymentRepository.findByPaymentStatusIgnoreCase(
                        "paid",
                        pageable
                );

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void testFindByReservationReservationId() {

        Reservation reservation1 = reservationRepository.save(

                Reservation.builder()
                        .guestName("Guest1")
                        .guestEmail("guest1@gmail.com")
                        .guestPhone("4444444444")
                        .checkInDate(LocalDate.now())
                        .checkOutDate(LocalDate.now().plusDays(1))
                        .build()
        );

        Reservation reservation2 = reservationRepository.save(

                Reservation.builder()
                        .guestName("Guest2")
                        .guestEmail("guest2@gmail.com")
                        .guestPhone("5555555555")
                        .checkInDate(LocalDate.now())
                        .checkOutDate(LocalDate.now().plusDays(1))
                        .build()
        );

        paymentRepository.saveAll(List.of(

                Payment.builder()
                        .amount(BigDecimal.valueOf(500))
                        .paymentDate(LocalDate.now())
                        .paymentStatus("SUCCESS")
                        .paymentMethod("UPI")
                        .reservation(reservation1)
                        .build(),

                Payment.builder()
                        .amount(BigDecimal.valueOf(700))
                        .paymentDate(LocalDate.now())
                        .paymentStatus("SUCCESS")
                        .paymentMethod("CARD")
                        .reservation(reservation1)
                        .build(),

                Payment.builder()
                        .amount(BigDecimal.valueOf(900))
                        .paymentDate(LocalDate.now())
                        .paymentStatus("FAILED")
                        .paymentMethod("CASH")
                        .reservation(reservation2)
                        .build()
        ));

        Pageable pageable = PageRequest.of(0, 10);

        Page<Payment> result =
                paymentRepository.findByReservation_ReservationId(
                        reservation1.getReservationId(),
                        pageable
                );

        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void testExistsByReservationReservationId_WhenExists() {

        Reservation reservation = reservationRepository.save(

                Reservation.builder()
                        .guestName("Exists User")
                        .guestEmail("exists@gmail.com")
                        .guestPhone("6666666666")
                        .checkInDate(LocalDate.now())
                        .checkOutDate(LocalDate.now().plusDays(2))
                        .build()
        );

        Payment payment = Payment.builder()
                .amount(BigDecimal.valueOf(1000))
                .paymentDate(LocalDate.now())
                .paymentStatus("SUCCESS")
                .paymentMethod("CARD")
                .reservation(reservation)
                .build();

        paymentRepository.save(payment);

        boolean exists =
                paymentRepository.existsByReservation_ReservationId(
                        reservation.getReservationId()
                );

        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByReservationReservationId_WhenNotExists() {

        boolean exists =
                paymentRepository.existsByReservation_ReservationId(99999);

        assertThat(exists).isFalse();
    }

    @Test
    void testDeletePayment() {

        Reservation reservation = reservationRepository.save(

                Reservation.builder()
                        .guestName("Delete User")
                        .guestEmail("delete@gmail.com")
                        .guestPhone("7777777777")
                        .checkInDate(LocalDate.now())
                        .checkOutDate(LocalDate.now().plusDays(1))
                        .build()
        );

        Payment payment = Payment.builder()
                .amount(BigDecimal.valueOf(250))
                .paymentDate(LocalDate.now())
                .paymentStatus("SUCCESS")
                .paymentMethod("UPI")
                .reservation(reservation)
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        paymentRepository.deleteById(savedPayment.getPaymentId());

        Optional<Payment> deletedPayment =
                paymentRepository.findById(savedPayment.getPaymentId());

        assertThat(deletedPayment).isNotPresent();
    }

    @Test
    void testCountPayments() {

        Reservation reservation1 = reservationRepository.save(

                Reservation.builder()
                        .guestName("Find1")
                        .guestEmail("find1@gmail.com")
                        .guestPhone("8888888888")
                        .checkInDate(LocalDate.now())
                        .checkOutDate(LocalDate.now().plusDays(1))
                        .build()
        );

        Reservation reservation2 = reservationRepository.save(

                Reservation.builder()
                        .guestName("Find2")
                        .guestEmail("find2@gmail.com")
                        .guestPhone("9999999999")
                        .checkInDate(LocalDate.now())
                        .checkOutDate(LocalDate.now().plusDays(1))
                        .build()
        );

        paymentRepository.saveAll(List.of(

                Payment.builder()
                        .amount(BigDecimal.valueOf(100))
                        .paymentDate(LocalDate.now())
                        .paymentStatus("SUCCESS")
                        .paymentMethod("CARD")
                        .reservation(reservation1)
                        .build(),

                Payment.builder()
                        .amount(BigDecimal.valueOf(200))
                        .paymentDate(LocalDate.now())
                        .paymentStatus("FAILED")
                        .paymentMethod("UPI")
                        .reservation(reservation2)
                        .build()
        ));

        assertThat(paymentRepository.count()).isEqualTo(2);
    }
}
