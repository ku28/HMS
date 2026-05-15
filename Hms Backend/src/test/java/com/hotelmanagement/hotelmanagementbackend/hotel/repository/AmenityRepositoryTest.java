package com.hotelmanagement.hotelmanagementbackend.hotel.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryDataJpaTest
@DisplayName("Amenity Repository Tests")
class AmenityRepositoryTest {

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    @DisplayName("shouldReturnAmenitiesByNameIgnoreCase")
    void shouldReturnAmenitiesByNameIgnoreCase() {

        persistAmenity("Free WiFi");
        persistAmenity("Airport Shuttle");

        Page<Amenity> result =
                amenityRepository.findByNameContainingIgnoreCase(
                        "wifi",
                        PageRequest.of(0, 10)
                );

        assertNotNull(result);

        assertTrue(result.getTotalElements() >= 1);

        assertTrue(
                result.getContent()
                        .stream()
                        .anyMatch(a ->
                                a.getName().equals("Free WiFi"))
        );
    }

    @Test
    @DisplayName("shouldReturnTrueWhenAmenityExists")
    void shouldReturnTrueWhenAmenityExists() {

        persistAmenity("Swimming Pool");

        boolean exists =
                amenityRepository.existsByName("Swimming Pool");

        assertTrue(exists);
    }

    @Test
    @DisplayName("shouldReturnFalseWhenAmenityDoesNotExist")
    void shouldReturnFalseWhenAmenityDoesNotExist() {

        boolean exists =
                amenityRepository.existsByName("Unknown Amenity");

        assertFalse(exists);
    }

    @Test
    @DisplayName("shouldReturnAmenityByName")
    void shouldReturnAmenityByName() {

        Amenity savedAmenity = persistAmenity("Breakfast");

        Optional<Amenity> result =
                amenityRepository.findByName("Breakfast");

        assertTrue(result.isPresent());

        Amenity amenity = result.get();

        assertEquals(savedAmenity.getAmenityId(), amenity.getAmenityId());
        assertEquals("Breakfast", amenity.getName());
    }

    @Test
    @DisplayName("shouldReturnAmenitiesAssignedToHotel")
    void shouldReturnAmenitiesAssignedToHotel() {

        Amenity pool = persistAmenity("Pool");
        Amenity parking = persistAmenity("Parking");

        Hotel hotel = Hotel.builder()
                .name("Taj Hotel")
                .location("Delhi")
                .description("Luxury Hotel")
                .build();

        hotel.getAmenities().add(pool);

        entityManager.persist(hotel);
        entityManager.flush();
        entityManager.clear();

        Page<Amenity> result =
                amenityRepository.findByHotels_HotelId(
                        hotel.getHotelId(),
                        PageRequest.of(0, 10)
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        Amenity amenity = result.getContent().getFirst();

        assertEquals("Pool", amenity.getName());
        assertNotEquals("Parking", amenity.getName());
    }

    @Test
    @DisplayName("shouldReturnAmenitiesAssignedToRoom")
    void shouldReturnAmenitiesAssignedToRoom() {

        Amenity balcony = persistAmenity("Balcony");
        Amenity minibar = persistAmenity("Minibar");

        Hotel hotel = persistHotel(
                "Lake View",
                "Pune"
        );

        RoomType roomType = persistRoomType("Deluxe");

        Room room = Room.builder()
                .roomNumber(301)
                .hotel(hotel)
                .roomType(roomType)
                .isAvailable(true)
                .build();

        room.getAmenities().add(balcony);
        balcony.getRooms().add(room);

        entityManager.persist(room);
        entityManager.flush();
        entityManager.clear();

        Page<Amenity> result =
                amenityRepository.findByRooms_RoomId(
                        room.getRoomId(),
                        PageRequest.of(0, 10)
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        Amenity amenity = result.getContent().getFirst();

        assertEquals("Balcony", amenity.getName());
        assertNotEquals("Minibar", amenity.getName());
    }

    private Amenity persistAmenity(String name) {

        Amenity amenity = Amenity.builder()
                .name(name)
                .description(name + " Description")
                .build();

        entityManager.persist(amenity);
        entityManager.flush();

        return amenity;
    }

    private Hotel persistHotel(
            String name,
            String location
    ) {

        Hotel hotel = Hotel.builder()
                .name(name)
                .location(location)
                .description(name + " Description")
                .build();

        entityManager.persist(hotel);
        entityManager.flush();

        return hotel;
    }

    private RoomType persistRoomType(String typeName) {

        RoomType roomType = RoomType.builder()
                .typeName(typeName)
                .description(typeName + " Room")
                .maxOccupancy(2)
                .pricePerNight(new BigDecimal("4500"))
                .build();

        entityManager.persist(roomType);
        entityManager.flush();

        return roomType;
    }
}
