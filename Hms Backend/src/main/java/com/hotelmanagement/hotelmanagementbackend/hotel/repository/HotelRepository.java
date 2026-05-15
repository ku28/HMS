package com.hotelmanagement.hotelmanagementbackend.hotel.repository;

import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.hotel.projection.HotelPublicProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "hotels", collectionResourceRel = "hotels",
        excerptProjection = HotelPublicProjection.class)
public interface HotelRepository extends JpaRepository<Hotel, Integer> {

    @RestResource(path = "by-location", rel = "by-location")
    Page<Hotel> findByLocationContainingIgnoreCase(@Param("location") String location, Pageable pageable);

    @RestResource(path = "by-name", rel = "by-name")
    Page<Hotel> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);

    @RestResource(path = "by-name-or-location", rel = "by-name-or-location")
    Page<Hotel> findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
            @Param("name") String name, @Param("location") String location, Pageable pageable);

    @RestResource(exported = false)
    boolean existsByNameAndLocation(String name, String location);

    @RestResource(path = "by-amenity", rel = "by-amenity")
    Page<Hotel> findByAmenities_AmenityId(@Param("amenityId") Integer amenityId, Pageable pageable);
}
