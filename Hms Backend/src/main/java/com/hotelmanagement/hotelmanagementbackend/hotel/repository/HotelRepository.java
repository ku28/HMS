package com.hotelmanagement.hotelmanagementbackend.hotel.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    Page<Hotel> findByLocationContainingIgnoreCase(String location, Pageable pageable);

    Page<Hotel> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Hotel> findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
            String name, String location, Pageable pageable);

    boolean existsByNameAndLocation(String name, String location);

    Page<Hotel> findByAmenities_AmenityId(Integer amenityId, Pageable pageable);
}
