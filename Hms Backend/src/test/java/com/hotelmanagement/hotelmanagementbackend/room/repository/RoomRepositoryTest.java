package com.hotelmanagement.hotelmanagementbackend.room.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryDataJpaTest
@DisplayName("RoomRepository Tests")
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByIsAvailableTrue should return available rooms")
    void findByIsAvailableTrueShouldReturnAvailableRooms() {
        Hotel hotel = persistHotel("City Stay", "Pune");
        RoomType deluxe = persistRoomType("Deluxe");
        persistRoom(101, hotel, deluxe, true);
        persistRoom(102, hotel, deluxe, false);

        Page<Room> result = roomRepository.findByIsAvailableTrue(PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Room::getRoomNumber)
                .containsExactly(101);
    }

    @Test
    @DisplayName("findByRoomType_RoomTypeIdAndIsAvailableTrue should return available rooms for type")
    void findByRoomTypeRoomTypeIdAndIsAvailableTrueShouldReturnAvailableRoomsForType() {
        Hotel hotel = persistHotel("Business Inn", "Mumbai");
        RoomType deluxe = persistRoomType("Deluxe");
        RoomType standard = persistRoomType("Standard");
        persistRoom(201, hotel, deluxe, true);
        persistRoom(202, hotel, deluxe, false);
        persistRoom(301, hotel, standard, true);

        Page<Room> result = roomRepository.findByRoomType_RoomTypeIdAndIsAvailableTrue(
                deluxe.getRoomTypeId(), PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Room::getRoomNumber)
                .containsExactly(201);
    }

    @Test
    @DisplayName("findByRoomType_RoomTypeId should return rooms for type")
    void findByRoomTypeRoomTypeIdShouldReturnRoomsForType() {
        Hotel hotel = persistHotel("Airport Stay", "Delhi");
        RoomType suite = persistRoomType("Suite");
        RoomType standard = persistRoomType("Standard");
        persistRoom(401, hotel, suite, true);
        persistRoom(402, hotel, suite, false);
        persistRoom(501, hotel, standard, true);

        Page<Room> result = roomRepository.findByRoomType_RoomTypeId(suite.getRoomTypeId(), PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Room::getRoomNumber)
                .containsExactlyInAnyOrder(401, 402);
    }

    @Test
    @DisplayName("findByAmenities_AmenityId should return rooms with amenity")
    void findByAmenitiesAmenityIdShouldReturnRoomsWithAmenity() {
        Hotel hotel = persistHotel("Amenity Stay", "Goa");
        RoomType deluxe = persistRoomType("Deluxe");
        Amenity balcony = persistAmenity("Balcony");
        Amenity minibar = persistAmenity("Minibar");
        Room balconyRoom = persistRoom(601, hotel, deluxe, true);
        Room minibarRoom = persistRoom(602, hotel, deluxe, true);
        balconyRoom.getAmenities().add(balcony);
        minibarRoom.getAmenities().add(minibar);
        entityManager.persistAndFlush(balconyRoom);
        entityManager.persistAndFlush(minibarRoom);
        entityManager.clear();

        Page<Room> result = roomRepository.findByAmenities_AmenityId(balcony.getAmenityId(), PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Room::getRoomNumber)
                .containsExactly(601);
    }

    @Test
    @DisplayName("existsByRoomNumberAndRoomType_RoomTypeId should report matching room number and type")
    void existsByRoomNumberAndRoomTypeRoomTypeIdShouldReportMatchingRoomNumberAndType() {
        Hotel hotel = persistHotel("Numbered Stay", "Kochi");
        RoomType deluxe = persistRoomType("Deluxe");
        RoomType standard = persistRoomType("Standard");
        persistRoom(701, hotel, deluxe, true);

        assertThat(roomRepository.existsByRoomNumberAndRoomType_RoomTypeId(701, deluxe.getRoomTypeId())).isTrue();
        assertThat(roomRepository.existsByRoomNumberAndRoomType_RoomTypeId(701, standard.getRoomTypeId())).isFalse();
    }

    @Test
    @DisplayName("findByHotel_HotelId should return rooms for hotel")
    void findByHotelHotelIdShouldReturnRoomsForHotel() {
        Hotel firstHotel = persistHotel("First Hotel", "Jaipur");
        Hotel secondHotel = persistHotel("Second Hotel", "Jaipur");
        RoomType deluxe = persistRoomType("Deluxe");
        persistRoom(801, firstHotel, deluxe, true);
        persistRoom(802, firstHotel, deluxe, false);
        persistRoom(901, secondHotel, deluxe, true);

        Page<Room> result = roomRepository.findByHotel_HotelId(firstHotel.getHotelId(), PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Room::getRoomNumber)
                .containsExactlyInAnyOrder(801, 802);
    }

    @Test
    @DisplayName("countByHotel_HotelIdAndIsAvailableTrue should count available hotel rooms")
    void countByHotelHotelIdAndIsAvailableTrueShouldCountAvailableHotelRooms() {
        Hotel hotel = persistHotel("Count Hotel", "Mysuru");
        RoomType deluxe = persistRoomType("Deluxe");
        persistRoom(1001, hotel, deluxe, true);
        persistRoom(1002, hotel, deluxe, true);
        persistRoom(1003, hotel, deluxe, false);

        long count = roomRepository.countByHotel_HotelIdAndIsAvailableTrue(hotel.getHotelId());

        assertThat(count).isEqualTo(2);
    }

    @Test
    @DisplayName("countByHotel_HotelId should count all hotel rooms")
    void countByHotelHotelIdShouldCountAllHotelRooms() {
        Hotel hotel = persistHotel("Total Hotel", "Nagpur");
        Hotel otherHotel = persistHotel("Other Hotel", "Nagpur");
        RoomType deluxe = persistRoomType("Deluxe");
        persistRoom(1101, hotel, deluxe, true);
        persistRoom(1102, hotel, deluxe, false);
        persistRoom(1201, otherHotel, deluxe, true);

        long count = roomRepository.countByHotel_HotelId(hotel.getHotelId());

        assertThat(count).isEqualTo(2);
    }

    private Hotel persistHotel(String name, String location) {
        return entityManager.persistAndFlush(Hotel.builder()
                .name(name)
                .location(location)
                .description(name + " description")
                .build());
    }

    private RoomType persistRoomType(String typeName) {
        return entityManager.persistAndFlush(RoomType.builder()
                .typeName(typeName)
                .description(typeName + " room")
                .maxOccupancy(2)
                .pricePerNight(new BigDecimal("175.00"))
                .build());
    }

    private Amenity persistAmenity(String name) {
        return entityManager.persistAndFlush(Amenity.builder()
                .name(name)
                .description(name + " description")
                .build());
    }

    private Room persistRoom(Integer roomNumber, Hotel hotel, RoomType roomType, boolean available) {
        return entityManager.persistAndFlush(Room.builder()
                .roomNumber(roomNumber)
                .hotel(hotel)
                .roomType(roomType)
                .isAvailable(available)
                .build());
    }
}
