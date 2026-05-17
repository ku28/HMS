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
        hotelRepository.save(Hotel.builder()
                .name("Repo Oberoi 7191")
                .location("Repo Delhi 7191")
                .description("Luxury hotel")
                .build());
        hotelRepository.save(Hotel.builder()
                .name("Repo Taj Palace 7191")
                .location("Repo Delhi 7191")
                .description("Premium hotel")
                .build());
        hotelRepository.save(Hotel.builder()
                .name("Repo Radisson Blu 7191")
                .location("Repo Mumbai 7191")
                .description("Business hotel")
                .build());
        hotelRepository.save(Hotel.builder()
                .name("Repo Hayat Regency 7191")
                .location("Repo Chandigarh 7191")
                .description("City hotel")
                .build());
    }

    @Test
    @DisplayName("shouldReturnHotelsByLocationIgnoreCase")
    void shouldReturnHotelsByLocationIgnoreCase() {

        Page<Hotel> result =
                hotelRepository.findByLocationContainingIgnoreCase(
                        "Repo Delhi 7191",
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

        assertEquals("Repo Radisson Blu 7191", hotel.getName());
        assertEquals("Repo Mumbai 7191", hotel.getLocation());
    }

    @Test
    @DisplayName("shouldReturnHotelsByNameOrLocation")
    void shouldReturnHotelsByNameOrLocation() {

        Page<Hotel> result =
                hotelRepository
                        .findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                                "Hayat",
                                "Repo Chandigarh 7191",
                                PageRequest.of(0, 10)
                        );

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());

        Hotel hotel = result.getContent().getFirst();

        assertEquals("Repo Hayat Regency 7191", hotel.getName());
        assertEquals("Repo Chandigarh 7191", hotel.getLocation());
    }

    @Test
    @DisplayName("shouldReturnTrueWhenHotelExists")
    void shouldReturnTrueWhenHotelExists() {

        boolean exists =
                hotelRepository.existsByNameAndLocation(
                        "Repo Oberoi 7191",
                        "Repo Delhi 7191"
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
