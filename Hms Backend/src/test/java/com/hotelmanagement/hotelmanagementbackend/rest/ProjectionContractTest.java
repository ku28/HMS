package com.hotelmanagement.hotelmanagementbackend.rest;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.hotel.projection.AmenityProjection;
import com.hotelmanagement.hotelmanagementbackend.hotel.projection.HotelProjection;
import com.hotelmanagement.hotelmanagementbackend.hotel.projection.HotelPublicProjection;
import com.hotelmanagement.hotelmanagementbackend.review.entity.Review;
import com.hotelmanagement.hotelmanagementbackend.review.projection.ReviewProjection;
import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import com.hotelmanagement.hotelmanagementbackend.room.projection.RoomProjection;
import com.hotelmanagement.hotelmanagementbackend.room.projection.RoomTypeProjection;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.rest.core.config.Projection;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Projection Contract Tests")
class ProjectionContractTest {

    @Test
    @DisplayName("hotel projections should expose public fields without recursive relationships")
    void hotelProjectionsShouldExposePublicFields() {
        assertProjection(HotelPublicProjection.class, "hotelPublic", Hotel.class);
        assertProjection(HotelProjection.class, "hotel", Hotel.class);

        assertThat(methodNames(HotelPublicProjection.class))
                .containsExactlyInAnyOrder("getHotelId", "getName", "getLocation", "getDescription");
        assertThat(methodNames(HotelProjection.class))
                .containsExactlyInAnyOrder("getHotelId", "getName", "getLocation", "getDescription", "getAmenities");
    }

    @Test
    @DisplayName("amenity and room-type projections should be flat catalog projections")
    void catalogProjectionsShouldBeFlat() {
        assertProjection(AmenityProjection.class, "amenity", Amenity.class);
        assertProjection(RoomTypeProjection.class, "roomType", RoomType.class);

        assertThat(methodNames(AmenityProjection.class))
                .containsExactlyInAnyOrder("getAmenityId", "getName", "getDescription");
        assertThat(methodNames(RoomTypeProjection.class))
                .containsExactlyInAnyOrder("getRoomTypeId", "getTypeName", "getDescription",
                        "getMaxOccupancy", "getPricePerNight");
    }

    @Test
    @DisplayName("room and review projections should use bounded nested projections")
    void roomAndReviewProjectionsShouldUseBoundedNestedProjections() {
        assertProjection(RoomProjection.class, "room", Room.class);
        assertProjection(ReviewProjection.class, "review", Review.class);

        assertThat(methodNames(RoomProjection.class))
                .containsExactlyInAnyOrder("getRoomId", "getRoomNumber", "getHotel",
                        "getRoomType", "getIsAvailable", "getAmenities");
        assertThat(methodNames(ReviewProjection.class))
                .containsExactlyInAnyOrder("getReviewId", "getReservation", "getRating",
                        "getComment", "getReviewDate");
    }

    private void assertProjection(Class<?> projectionType, String name, Class<?> entityType) {
        Projection projection = projectionType.getAnnotation(Projection.class);
        assertThat(projection).isNotNull();
        assertThat(projection.name()).isEqualTo(name);
        assertThat(projection.types()).contains(entityType);
    }

    private String[] methodNames(Class<?> projectionType) {
        return java.util.Arrays.stream(projectionType.getDeclaredMethods())
                .map(java.lang.reflect.Method::getName)
                .toArray(String[]::new);
    }
}
