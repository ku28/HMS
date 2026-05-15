package com.hotelmanagement.hotelmanagementbackend.hotel.repository;

import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.junit.jupiter.api.Assertions.*;

@RepositoryDataJpaTest
class HotelRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    @BeforeEach
    void setUp() {
        hotelRepository.deleteAll();
        hotelRepository.save(Hotel.builder()
                .name("Oberoi")
                .location("Delhi")
                .description("Luxury hotel")
                .build());
        hotelRepository.save(Hotel.builder()
                .name("Taj Palace")
                .location("Delhi")
                .description("Premium hotel")
                .build());
        hotelRepository.save(Hotel.builder()
                .name("Radisson Blu")
                .location("Mumbai")
                .description("Business hotel")
                .build());
        hotelRepository.save(Hotel.builder()
                .name("Hayat Regency")
                .location("Chandigarh")
                .description("City hotel")
                .build());
    }

    @Test
    @DisplayName("shouldReturnHotelsByLocationIgnoreCase")
    void shouldReturnHotelsByLocationIgnoreCase() {

        Page<Hotel> result =
                hotelRepository.findByLocationContainingIgnoreCase(
                        "Delhi",
                        PageRequest.of(0, 10)
                );

        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("shouldReturnHotelsByNameIgnoreCase")
    void shouldReturnHotelsByNameIgnoreCase() {

        Page<Hotel> result =
                hotelRepository.findByNameContainingIgnoreCase(
                        "Radisson",
                        PageRequest.of(0, 10)
                );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        Hotel hotel = result.getContent().getFirst();

        assertEquals("Radisson Blu", hotel.getName());
        assertEquals("Mumbai", hotel.getLocation());
    }

    @Test
    @DisplayName("shouldReturnHotelsByNameOrLocation")
    void shouldReturnHotelsByNameOrLocation() {

        Page<Hotel> result =
                hotelRepository
                        .findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                                "Hayat",
                                "Chandigarh",
                                PageRequest.of(0, 10)
                        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        Hotel hotel = result.getContent().getFirst();

        assertEquals("Hayat Regency", hotel.getName());
        assertEquals("Chandigarh", hotel.getLocation());
    }

    @Test
    @DisplayName("shouldReturnTrueWhenHotelExists")
    void shouldReturnTrueWhenHotelExists() {

        boolean exists =
                hotelRepository.existsByNameAndLocation(
                        "Oberoi",
                        "Delhi"
                );

        assertTrue(exists);
    }

    @Test
    @DisplayName("shouldReturnFalseWhenHotelDoesNotExist")
    void shouldReturnFalseWhenHotelDoesNotExist() {

        boolean exists =
                hotelRepository.existsByNameAndLocation(
                        "Unknown Hotel",
                        "Punjab"
                );

        assertFalse(exists);
    }
}
