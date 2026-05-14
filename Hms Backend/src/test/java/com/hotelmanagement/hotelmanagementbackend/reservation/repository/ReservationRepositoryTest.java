package com.hotelmanagement.hotelmanagementbackend.reservation.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
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
@DisplayName("ReservationRepository Tests")
class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByGuestEmailIgnoreCase should match guest email regardless of case")
    void findByGuestEmailIgnoreCaseShouldMatchGuestEmailRegardlessOfCase() {
        Room room = persistRoom(101);
        persistReservation("John Doe", "John@example.com", room, LocalDate.of(2026, 6, 1), LocalDate.of(2026, 6, 4));
        persistReservation("Jane Doe", "jane@example.com", room, LocalDate.of(2026, 6, 6), LocalDate.of(2026, 6, 8));

        Page<Reservation> result = reservationRepository.findByGuestEmailIgnoreCase(
                "john@EXAMPLE.com", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Reservation::getGuestName)
                .containsExactly("John Doe");
    }

    @Test
    @DisplayName("findByCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual should return reservations inside range")
    void findByCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqualShouldReturnReservationsInsideRange() {
        Room room = persistRoom(201);
        persistReservation("Inside Guest", "inside@example.com", room,
                LocalDate.of(2026, 7, 10), LocalDate.of(2026, 7, 12));
        persistReservation("Early Guest", "early@example.com", room,
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 12));
        persistReservation("Late Guest", "late@example.com", room,
                LocalDate.of(2026, 7, 15), LocalDate.of(2026, 7, 22));

        Page<Reservation> result = reservationRepository.findByCheckInDateGreaterThanEqualAndCheckOutDateLessThanEqual(
                LocalDate.of(2026, 7, 5), LocalDate.of(2026, 7, 20), PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Reservation::getGuestName)
                .containsExactly("Inside Guest");
    }

    @Test
    @DisplayName("findByRoom_RoomId should return reservations for room")
    void findByRoomRoomIdShouldReturnReservationsForRoom() {
        Room firstRoom = persistRoom(301);
        Room secondRoom = persistRoom(302);
        persistReservation("First Guest", "first@example.com", firstRoom,
                LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 3));
        persistReservation("Second Guest", "second@example.com", secondRoom,
                LocalDate.of(2026, 8, 4), LocalDate.of(2026, 8, 6));

        Page<Reservation> result = reservationRepository.findByRoom_RoomId(firstRoom.getRoomId(), PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Reservation::getGuestName)
                .containsExactly("First Guest");
    }

    @Test
    @DisplayName("existsByRoom_RoomIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual should detect overlaps")
    void existsByRoomRoomIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqualShouldDetectOverlaps() {
        Room room = persistRoom(401);
        persistReservation("Booked Guest", "booked@example.com", room,
                LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 15));

        boolean overlaps = reservationRepository.existsByRoom_RoomIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
                room.getRoomId(), LocalDate.of(2026, 9, 12), LocalDate.of(2026, 9, 11));
        boolean doesNotOverlap = reservationRepository.existsByRoom_RoomIdAndCheckInDateLessThanEqualAndCheckOutDateGreaterThanEqual(
                room.getRoomId(), LocalDate.of(2026, 9, 20), LocalDate.of(2026, 9, 18));

        assertThat(overlaps).isTrue();
        assertThat(doesNotOverlap).isFalse();
    }

    @Test
    @DisplayName("count should return total reservation count")
    void countShouldReturnTotalReservationCount() {
        Room room = persistRoom(501);
        persistReservation("First Guest", "first-count@example.com", room,
                LocalDate.of(2026, 10, 1), LocalDate.of(2026, 10, 3));
        persistReservation("Second Guest", "second-count@example.com", room,
                LocalDate.of(2026, 10, 5), LocalDate.of(2026, 10, 7));

        long count = reservationRepository.count();

        assertThat(count).isEqualTo(2);
    }

    private Reservation persistReservation(String guestName, String guestEmail, Room room,
                                           LocalDate checkInDate, LocalDate checkOutDate) {
        return entityManager.persistAndFlush(Reservation.builder()
                .guestName(guestName)
                .guestEmail(guestEmail)
                .guestPhone("9999999999")
                .room(room)
                .checkInDate(checkInDate)
                .checkOutDate(checkOutDate)
                .build());
    }

    private Room persistRoom(Integer roomNumber) {
        Hotel hotel = entityManager.persistAndFlush(Hotel.builder()
                .name("Reservation Hotel " + roomNumber)
                .location("Bengaluru")
                .description("Reservation test hotel")
                .build());
        RoomType roomType = entityManager.persistAndFlush(RoomType.builder()
                .typeName("Reservation Type " + roomNumber)
                .description("Reservation test type")
                .maxOccupancy(2)
                .pricePerNight(new BigDecimal("220.00"))
                .build());
        return entityManager.persistAndFlush(Room.builder()
                .roomNumber(roomNumber)
                .hotel(hotel)
                .roomType(roomType)
                .isAvailable(true)
                .build());
    }
}
