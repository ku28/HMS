package com.hotelmanagement.hotelmanagementbackend.room.repository;

import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryDataJpaTest
class RoomTypeRepositoryTest {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("shouldReturnRoomTypesByNameIgnoreCase")
    void shouldReturnRoomTypesByNameIgnoreCase() {

        // Arrange
        RoomType deluxe = RoomType.builder()
                .typeName("Repo Deluxe 7191")
                .description("Luxury Room")
                .maxOccupancy(4)
                .pricePerNight(new BigDecimal("5000"))
                .build();

        RoomType standard = RoomType.builder()
                .typeName("Repo Standard 7191")
                .description("Standard Room")
                .maxOccupancy(2)
                .pricePerNight(new BigDecimal("3000"))
                .build();

        roomTypeRepository.save(deluxe);
        roomTypeRepository.save(standard);

        // Act
        Page<RoomType> result =
                roomTypeRepository.findByTypeNameContainingIgnoreCase(
                        "Repo Deluxe 7191",
                        PageRequest.of(0, 10)
                );

        // Assert
        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().get(0).getTypeName())
                .isEqualTo("Repo Deluxe 7191");
    }

    @Test
    @DisplayName("shouldCheckIfRoomTypeExists")
    void shouldCheckIfRoomTypeExists() {

        // Arrange
        RoomType deluxe = RoomType.builder()
                .typeName("Repo Exists Deluxe 7191")
                .description("Luxury Room")
                .maxOccupancy(4)
                .pricePerNight(new BigDecimal("5000"))
                .build();

        roomTypeRepository.save(deluxe);

        // Act
        boolean exists =
                roomTypeRepository.existsByTypeName("Repo Exists Deluxe 7191");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("shouldReturnFalseWhenRoomTypeDoesNotExist")
    void shouldReturnFalseWhenRoomTypeDoesNotExist() {

        // Act
        boolean exists =
                roomTypeRepository.existsByTypeName("Repo Missing Suite 7191");

        // Assert
        assertThat(exists).isFalse();
    }
}
