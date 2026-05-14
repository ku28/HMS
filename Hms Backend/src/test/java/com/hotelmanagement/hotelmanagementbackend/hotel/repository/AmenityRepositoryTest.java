package com.hotelmanagement.hotelmanagementbackend.hotel.repository;

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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryDataJpaTest
@DisplayName("AmenityRepository Tests")
class AmenityRepositoryTest {

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByNameContainingIgnoreCase should match partial names")
    void findByNameContainingIgnoreCaseShouldMatchPartialNames() {
        persistAmenity("Free WiFi");
        persistAmenity("Airport Shuttle");

        Page<Amenity> result = amenityRepository.findByNameContainingIgnoreCase("wifi", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Amenity::getName)
                .containsExactly("Free WiFi");
    }

    @Test
    @DisplayName("existsByName should report whether amenity name exists")
    void existsByNameShouldReportWhetherAmenityNameExists() {
        persistAmenity("Pool");

        assertThat(amenityRepository.existsByName("Pool")).isTrue();
        assertThat(amenityRepository.existsByName("Spa")).isFalse();
    }

    @Test
    @DisplayName("findByName should return matching amenity")
    void findByNameShouldReturnMatchingAmenity() {
        Amenity amenity = persistAmenity("Breakfast");

        Optional<Amenity> result = amenityRepository.findByName("Breakfast");

        assertThat(result).isPresent();
        assertThat(result.get().getAmenityId()).isEqualTo(amenity.getAmenityId());
    }

    @Test
    @DisplayName("findByHotels_HotelId should return amenities assigned to hotel")
    void findByHotelsHotelIdShouldReturnAmenitiesAssignedToHotel() {
        Amenity pool = persistAmenity("Pool");
        Amenity parking = persistAmenity("Parking");
        Hotel hotel = Hotel.builder()
                .name("City Grand")
                .location("Mumbai")
                .description("Business hotel")
                .build();
        hotel.getAmenities().add(pool);
        entityManager.persistAndFlush(hotel);
        entityManager.clear();

        Page<Amenity> result = amenityRepository.findByHotels_HotelId(hotel.getHotelId(), PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Amenity::getName)
                .containsExactly("Pool");
        assertThat(result.getContent())
                .extracting(Amenity::getName)
                .doesNotContain(parking.getName());
    }

    @Test
    @DisplayName("findByRooms_RoomId should return amenities assigned to room")
    void findByRoomsRoomIdShouldReturnAmenitiesAssignedToRoom() {
        Amenity balcony = persistAmenity("Balcony");
        Amenity minibar = persistAmenity("Minibar");
        Hotel hotel = persistHotel("Lake View", "Pune");
        RoomType roomType = persistRoomType("Deluxe");
        Room room = Room.builder()
                .roomNumber(301)
                .hotel(hotel)
                .roomType(roomType)
                .isAvailable(true)
                .build();
        room.getAmenities().add(balcony);
        entityManager.persistAndFlush(room);
        entityManager.clear();

        Page<Amenity> result = amenityRepository.findByRooms_RoomId(room.getRoomId(), PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Amenity::getName)
                .containsExactly("Balcony");
        assertThat(result.getContent())
                .extracting(Amenity::getName)
                .doesNotContain(minibar.getName());
    }

    private Amenity persistAmenity(String name) {
        return entityManager.persistAndFlush(Amenity.builder()
                .name(name)
                .description(name + " description")
                .build());
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
                .pricePerNight(new BigDecimal("150.00"))
                .build());
    }
}
