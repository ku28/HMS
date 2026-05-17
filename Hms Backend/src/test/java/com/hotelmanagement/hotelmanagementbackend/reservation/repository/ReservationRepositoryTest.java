package com.hotelmanagement.hotelmanagementbackend.reservation.repository;

import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import com.hotelmanagement.hotelmanagementbackend.reservation.entity.Reservation;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import com.hotelmanagement.hotelmanagementbackend.room.repository.RoomRepository;
import com.hotelmanagement.hotelmanagementbackend.room.repository.RoomTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryDataJpaTest
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    private Room room;

    @BeforeEach
    void setup() {
        RoomType roomType = roomTypeRepository.save(

                RoomType.builder()
                        .typeName("REPO RESERVATION DELUXE 7191")
                        .description("Luxury Room")
                        .maxOccupancy(2)
                        .pricePerNight(BigDecimal.valueOf(5000))
                        .build()
        );

        room = roomRepository.save(

                Room.builder()
                        .roomNumber(719101)
                        .isAvailable(true)
                        .roomType(roomType)
                        .build()
        );
    }

    @Test
    @DisplayName("Test findByGuestEmailIgnoreCase")
    void testFindByGuestEmailIgnoreCase() {

        Reservation reservation = Reservation.builder()
                .guestName("John Doe")
                .guestEmail("repo-john-7191@example.com")
                .guestPhone("9876543210")
                .checkInDate(LocalDate.of(2026, 5, 20))
                .checkOutDate(LocalDate.of(2026, 5, 25))
                .room(room)
                .build();

        reservationRepository.save(reservation);

        Page<Reservation> result =
                reservationRepository.findByGuestEmailIgnoreCase(
                        "REPO-JOHN-7191@EXAMPLE.COM",
                        PageRequest.of(0, 10)
                );

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Test date range search")
    void testFindByCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual() {

        Reservation reservation = Reservation.builder()
                .guestName("Alice")
                .guestEmail("repo-alice-7191@example.com")
                .guestPhone("9999999999")
                .checkInDate(LocalDate.of(2026, 6, 1))
                .checkOutDate(LocalDate.of(2026, 6, 5))
                .room(room)
                .build();

        reservationRepository.save(reservation);

        Page<Reservation> result =
                reservationRepository
                        .findByCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual(
                                LocalDate.of(2026, 6, 1),
                                LocalDate.of(2026, 6, 10),
                                PageRequest.of(0, 10)
                        );

        assertTrue(result.getContent().stream()
                .anyMatch(saved -> "repo-alice-7191@example.com".equals(saved.getGuestEmail())));
    }

    @Test
    @DisplayName("Test findByRoom_RoomId")
    void testFindByRoomRoomId() {

        Reservation reservation = Reservation.builder()
                .guestName("Room User")
                .guestEmail("room@example.com")
                .guestPhone("7777777777")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(2))
                .room(room)
                .build();

        reservationRepository.save(reservation);

        Page<Reservation> result =
                reservationRepository.findByRoom_RoomId(
                        room.getRoomId(),
                        PageRequest.of(0, 10)
                );

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Test existsByRoomIdAndDateRange")
    void testExistsByRoomIdAndDateRange() {

        Reservation reservation = Reservation.builder()
                .guestName("Booked User")
                .guestEmail("booked@example.com")
                .guestPhone("6666666666")
                .checkInDate(LocalDate.of(2026, 7, 10))
                .checkOutDate(LocalDate.of(2026, 7, 15))
                .room(room)
                .build();

        reservationRepository.save(reservation);

        boolean exists =
                reservationRepository
                        .existsByRoom_RoomIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
                                room.getRoomId(),
                                LocalDate.of(2026, 7, 14),
                                LocalDate.of(2026, 7, 12)
                        );

        assertTrue(exists);
    }

    @Test
    @DisplayName("Test reservation count")
    void testCount() {

        Reservation reservation = Reservation.builder()
                .guestName("Sara")
                .guestEmail("sara@example.com")
                .guestPhone("8888888888")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(2))
                .room(room)
                .build();

        reservationRepository.save(reservation);

        Page<Reservation> result =
                reservationRepository.findByGuestEmailIgnoreCase(
                        "sara@example.com",
                        PageRequest.of(0, 10)
                );

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("Test delete reservation")
    void testDeleteReservation() {

        Reservation reservation = Reservation.builder()
                .guestName("Delete User")
                .guestEmail("delete@example.com")
                .guestPhone("5555555555")
                .checkInDate(LocalDate.now())
                .checkOutDate(LocalDate.now().plusDays(1))
                .room(room)
                .build();

        Reservation savedReservation =
                reservationRepository.save(reservation);

        reservationRepository.deleteById(
                savedReservation.getReservationId()
        );

        boolean exists =
                reservationRepository.findById(
                        savedReservation.getReservationId()
                ).isPresent();

        assertFalse(exists);
    }
}
