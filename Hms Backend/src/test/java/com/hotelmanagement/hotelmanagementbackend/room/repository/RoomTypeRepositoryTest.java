package com.hotelmanagement.hotelmanagementbackend.room.repository;

import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
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
@DisplayName("RoomTypeRepository Tests")
class RoomTypeRepositoryTest {

    @Autowired
    private RoomTypeRepository roomTypeRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    @DisplayName("findByTypeNameContainingIgnoreCase should match partial room type names")
    void findByTypeNameContainingIgnoreCaseShouldMatchPartialRoomTypeNames() {
        persistRoomType("Deluxe Suite");
        persistRoomType("Standard Room");

        Page<RoomType> result = roomTypeRepository.findByTypeNameContainingIgnoreCase("suite", PageRequest.of(0, 10));

        assertThat(result.getContent())
                .extracting(RoomType::getTypeName)
                .containsExactly("Deluxe Suite");
    }

    @Test
    @DisplayName("existsByTypeName should report whether room type exists")
    void existsByTypeNameShouldReportWhetherRoomTypeExists() {
        persistRoomType("Executive Room");

        assertThat(roomTypeRepository.existsByTypeName("Executive Room")).isTrue();
        assertThat(roomTypeRepository.existsByTypeName("Presidential Suite")).isFalse();
    }

    private RoomType persistRoomType(String typeName) {
        return entityManager.persistAndFlush(RoomType.builder()
                .typeName(typeName)
                .description(typeName + " description")
                .maxOccupancy(2)
                .pricePerNight(new BigDecimal("200.00"))
                .build());
    }
}
