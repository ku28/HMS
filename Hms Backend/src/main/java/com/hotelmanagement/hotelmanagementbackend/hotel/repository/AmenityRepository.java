package com.hotelmanagement.hotelmanagementbackend.hotel.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AmenityRepository extends JpaRepository<Amenity, Integer> {

    Page<Amenity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByName(String name);

    Optional<Amenity> findByName(String name);

    Page<Amenity> findByHotels_HotelId(Integer hotelId, Pageable pageable);

    Page<Amenity> findByRooms_RoomId(Integer roomId, Pageable pageable);
}
