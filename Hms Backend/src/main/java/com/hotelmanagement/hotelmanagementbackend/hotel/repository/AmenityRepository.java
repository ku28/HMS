package com.hotelmanagement.hotelmanagementbackend.hotel.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.projection.AmenityProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RepositoryRestResource(path = "amenities", collectionResourceRel = "amenities",
        excerptProjection = AmenityProjection.class)
public interface AmenityRepository extends JpaRepository<Amenity, Integer> {

    @RestResource(path = "by-name", rel = "by-name")
    Page<Amenity> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @RestResource(exported = false)
    boolean existsByName(String name);

    @RestResource(exported = false)
    Optional<Amenity> findByName(String name);

    @RestResource(path = "by-hotel", rel = "by-hotel")
    Page<Amenity> findByHotels_HotelId(@Param("hotelId") Integer hotelId, Pageable pageable);

    @RestResource(path = "by-room", rel = "by-room")
    Page<Amenity> findByRooms_RoomId(@Param("roomId") Integer roomId, Pageable pageable);
}
