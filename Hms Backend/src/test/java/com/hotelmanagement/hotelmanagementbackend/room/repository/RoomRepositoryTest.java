package com.hotelmanagement.hotelmanagementbackend.room.repository;

import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
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
class RoomRepositoryTest {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @BeforeEach
    void setUp() {
        roomRepository.deleteAll();
        roomTypeRepository.deleteAll();
    }

    @Test
    @DisplayName("shouldReturnAvailableRooms")
    void shouldReturnAvailableRooms() {

        // Arrange
        Room room1 = Room.builder()
                .roomNumber(101)
                .isAvailable(true)
                .build();

        Room room2 = Room.builder()
                .roomNumber(102)
                .isAvailable(false)
                .build();

        Room room3 = Room.builder()
                .roomNumber(103)
                .isAvailable(true)
                .build();

        roomRepository.save(room1);
        roomRepository.save(room2);
        roomRepository.save(room3);

        // Act
        Page<Room> result =
                roomRepository.findByIsAvailableTrue(
                        PageRequest.of(0, 10)
                );

        // Assert
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("shouldReturnRoomsByRoomType")
    void shouldReturnRoomsByRoomType() {

        // Arrange
        RoomType deluxe = RoomType.builder()
                .typeName("Deluxe")
                .description("Luxury")
                .maxOccupancy(4)
                .pricePerNight(new BigDecimal("5000"))
                .build();

        RoomType savedRoomType =
                roomTypeRepository.save(deluxe);

        Room room1 = Room.builder()
                .roomNumber(201)
                .roomType(savedRoomType)
                .isAvailable(true)
                .build();

        Room room2 = Room.builder()
                .roomNumber(202)
                .roomType(savedRoomType)
                .isAvailable(false)
                .build();

        roomRepository.save(room1);
        roomRepository.save(room2);

        // Act
        Page<Room> result =
                roomRepository.findByRoomType_RoomTypeId(
                        savedRoomType.getRoomTypeId(),
                        PageRequest.of(0, 10)
                );

        // Assert
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("shouldReturnAvailableRoomsByRoomType")
    void shouldReturnAvailableRoomsByRoomType() {

        // Arrange
        RoomType deluxe = RoomType.builder()
                .typeName("Deluxe")
                .description("Luxury")
                .maxOccupancy(4)
                .pricePerNight(new BigDecimal("5000"))
                .build();

        RoomType savedRoomType =
                roomTypeRepository.save(deluxe);

        Room room1 = Room.builder()
                .roomNumber(301)
                .roomType(savedRoomType)
                .isAvailable(true)
                .build();

        Room room2 = Room.builder()
                .roomNumber(302)
                .roomType(savedRoomType)
                .isAvailable(false)
                .build();

        roomRepository.save(room1);
        roomRepository.save(room2);

        // Act
        Page<Room> result =
                roomRepository.findByRoomType_RoomTypeIdAndIsAvailableTrue(
                        savedRoomType.getRoomTypeId(),
                        PageRequest.of(0, 10)
                );

        // Assert
        assertThat(result.getContent()).hasSize(1);

        assertThat(result.getContent().get(0).getRoomNumber())
                .isEqualTo(301);
    }

    @Test
    @DisplayName("shouldCheckIfRoomExists")
    void shouldCheckIfRoomExists() {

        // Arrange
        RoomType deluxe = RoomType.builder()
                .typeName("Deluxe")
                .description("Luxury")
                .maxOccupancy(4)
                .pricePerNight(new BigDecimal("5000"))
                .build();

        RoomType savedRoomType =
                roomTypeRepository.save(deluxe);

        Room room = Room.builder()
                .roomNumber(401)
                .roomType(savedRoomType)
                .isAvailable(true)
                .build();

        roomRepository.save(room);

        // Act
        boolean exists =
                roomRepository.existsByRoomNumberAndRoomType_RoomTypeId(
                        401,
                        savedRoomType.getRoomTypeId()
                );

        // Assert
        assertThat(exists).isTrue();
    }
}
