package com.hotelmanagement.hotelmanagementbackend.hotel.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryDataJpaTest
@DisplayName("HotelRepository Tests")
class HotelRepositoryTest {

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByLocationContainingIgnoreCase should match partial locations")
    void findByLocationContainingIgnoreCaseShouldMatchPartialLocations() {
        persistHotel("Sea Palace", "Mumbai Central");
        persistHotel("Hill Palace", "Shimla");

        Page<Hotel> result = hotelRepository.findByLocationContainingIgnoreCase("mumbai", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Hotel::getName)
                .containsExactly("Sea Palace");
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCase should match partial names")
    void findByNameContainingIgnoreCaseShouldMatchPartialNames() {
        persistHotel("Royal Orchid", "Bengaluru");
        persistHotel("Budget Inn", "Bengaluru");

        Page<Hotel> result = hotelRepository.findByNameContainingIgnoreCase("orchid", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Hotel::getName)
                .containsExactly("Royal Orchid");
    }

    @Test
    @DisplayName("findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase should match name or location")
    void findByNameContainingIgnoreCaseOrLocationContainingIgnoreCaseShouldMatchNameOrLocation() {
        persistHotel("Airport Residency", "Delhi");
        persistHotel("City Lodge", "Chennai");
        persistHotel("Mountain Stay", "Manali");

        Page<Hotel> result = hotelRepository
                .findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase("airport", "chennai", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Hotel::getName)
                .containsExactlyInAnyOrder("Airport Residency", "City Lodge");
    }

    @Test
    @DisplayName("existsByNameAndLocation should report matching hotel and location")
    void existsByNameAndLocationShouldReportMatchingHotelAndLocation() {
        persistHotel("Lake House", "Udaipur");

        assertThat(hotelRepository.existsByNameAndLocation("Lake House", "Udaipur")).isTrue();
        assertThat(hotelRepository.existsByNameAndLocation("Lake House", "Jaipur")).isFalse();
    }

    @Test
    @DisplayName("findByAmenities_AmenityId should return hotels with amenity")
    void findByAmenitiesAmenityIdShouldReturnHotelsWithAmenity() {
        Amenity pool = entityManager.persistAndFlush(Amenity.builder()
                .name("Pool")
                .description("Outdoor pool")
                .build());
        Amenity gym = entityManager.persistAndFlush(Amenity.builder()
                .name("Gym")
                .description("Fitness center")
                .build());
        Hotel poolHotel = persistHotel("Poolside Resort", "Goa");
        Hotel gymHotel = persistHotel("Fitness Suites", "Hyderabad");
        poolHotel.getAmenities().add(pool);
        gymHotel.getAmenities().add(gym);
        entityManager.persistAndFlush(poolHotel);
        entityManager.persistAndFlush(gymHotel);
        entityManager.clear();

        Page<Hotel> result = hotelRepository.findByAmenities_AmenityId(pool.getAmenityId(), PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(Hotel::getName)
                .containsExactly("Poolside Resort");
    }

    private Hotel persistHotel(String name, String location) {
        return entityManager.persistAndFlush(Hotel.builder()
                .name(name)
                .location(location)
                .description(name + " description")
                .build());
    }
}
