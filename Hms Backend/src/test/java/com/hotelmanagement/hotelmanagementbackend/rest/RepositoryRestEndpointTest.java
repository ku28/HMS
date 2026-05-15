package com.hotelmanagement.hotelmanagementbackend.rest;

import com.hotelmanagement.hotelmanagementbackend.config.DotenvTestPropertyInitializer;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.AmenityRepository;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.HotelRepository;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import com.hotelmanagement.hotelmanagementbackend.room.repository.RoomRepository;
import com.hotelmanagement.hotelmanagementbackend.room.repository.RoomTypeRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.cache.type=none"
})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
@ContextConfiguration(initializers = DotenvTestPropertyInitializer.class)
@Transactional
@DisplayName("Spring Data REST Endpoint Tests")
class RepositoryRestEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private AmenityRepository amenityRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Test
    @DisplayName("GET /api/hotels should return HAL paginated hotels with sorting")
    void hotelsEndpointShouldReturnPagedHotelsWithSorting() throws Exception {
        hotelRepository.save(Hotel.builder()
                .name("Zeta Suites")
                .location("Mumbai")
                .description("Business hotel")
                .build());
        hotelRepository.save(Hotel.builder()
                .name("Alpha Palace")
                .location("Delhi")
                .description("City hotel")
                .build());

        mockMvc.perform(get("/api/hotels")
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "name,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.hotels[0].name").value("Alpha Palace"))
                .andExpect(jsonPath("$.page.size").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    @DisplayName("GET /api/hotels should use public hotel projection by default")
    void hotelsEndpointShouldUsePublicProjectionByDefault() throws Exception {
        hotelRepository.save(Hotel.builder()
                .name("Projection Suites")
                .location("Bengaluru")
                .description("Projection test")
                .build());

        mockMvc.perform(get("/api/hotels")
                        .param("projection", "hotelPublic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.hotels[0].hotelId").exists())
                .andExpect(jsonPath("$._embedded.hotels[0].name").value("Projection Suites"))
                .andExpect(jsonPath("$._embedded.hotels[0].amenities").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/hotels/search/by-location should filter hotels by location")
    void hotelSearchEndpointShouldFilterByLocation() throws Exception {
        hotelRepository.save(Hotel.builder()
                .name("Sea View")
                .location("Mumbai Central")
                .description("Near the sea")
                .build());
        hotelRepository.save(Hotel.builder()
                .name("Hill View")
                .location("Shimla")
                .description("Near the hills")
                .build());

        mockMvc.perform(get("/api/hotels/search/by-location")
                        .param("location", "mumbai")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.hotels[0].name").value("Sea View"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/amenities/search/by-name should expose simple CRUD search")
    void amenitySearchEndpointShouldFilterByName() throws Exception {
        amenityRepository.save(Amenity.builder()
                .name("Pool")
                .description("Outdoor pool")
                .build());
        amenityRepository.save(Amenity.builder()
                .name("Gym")
                .description("Fitness center")
                .build());

        mockMvc.perform(get("/api/amenities/search/by-name")
                        .param("name", "pool"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.amenities[0].name").value("Pool"));
    }

    @Test
    @DisplayName("GET /api/room-types should expose paginated room type projection")
    void roomTypeEndpointShouldExposeProjection() throws Exception {
        roomTypeRepository.save(RoomType.builder()
                .typeName("Executive")
                .description("Executive room")
                .maxOccupancy(3)
                .pricePerNight(new BigDecimal("310.00"))
                .build());

        mockMvc.perform(get("/api/room-types")
                        .param("projection", "roomType"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.roomTypes[0].roomTypeId").exists())
                .andExpect(jsonPath("$._embedded.roomTypes[0].typeName").value("Executive"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    @DisplayName("GET /api/rooms/search/available should expose read-only hybrid room search")
    void roomSearchEndpointShouldExposeAvailableRooms() throws Exception {
        Hotel hotel = hotelRepository.save(Hotel.builder()
                .name("Hybrid Hotel")
                .location("Pune")
                .description("Hybrid test")
                .build());
        RoomType roomType = roomTypeRepository.save(RoomType.builder()
                .typeName("Deluxe")
                .description("Deluxe room")
                .maxOccupancy(2)
                .pricePerNight(new BigDecimal("210.00"))
                .build());
        roomRepository.save(Room.builder()
                .roomNumber(101)
                .hotel(hotel)
                .roomType(roomType)
                .isAvailable(true)
                .build());

        mockMvc.perform(get("/api/rooms/search/available"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.rooms[0].roomNumber").value(101));
    }

    @Test
    @DisplayName("POST /api/rooms should be disabled because room writes stay in controller/service")
    void roomRepositoryWriteEndpointShouldBeDisabled() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roomNumber\":101,\"isAvailable\":true}"))
                .andExpect(status().isMethodNotAllowed());
    }

    @Test
    @DisplayName("legacy DTO CRUD endpoints should not be exposed")
    void legacyDtoCrudEndpointsShouldNotBeExposed() throws Exception {
        mockMvc.perform(get("/api/room/all"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/amenity/all"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get("/api/RoomType/all"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/users should not be exposed by Spring Data REST")
    void userRepositoryEndpointShouldNotBeExposed() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isNotFound());
    }
}
