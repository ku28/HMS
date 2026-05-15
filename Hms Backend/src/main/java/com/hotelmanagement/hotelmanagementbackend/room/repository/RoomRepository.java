package com.hotelmanagement.hotelmanagementbackend.room.repository;

import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import com.hotelmanagement.hotelmanagementbackend.room.projection.RoomProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "rooms", collectionResourceRel = "rooms",
        excerptProjection = RoomProjection.class)
public interface RoomRepository extends JpaRepository<Room, Integer> {

    @RestResource(path = "all", rel = "all")
    Page<Room> findByRoomIdGreaterThan(@Param("roomId") Integer roomId, Pageable pageable);

    @RestResource(path = "available", rel = "available")
    Page<Room> findByIsAvailableTrue(Pageable pageable);

    @RestResource(path = "available-by-room-type", rel = "available-by-room-type")
    Page<Room> findByRoomType_RoomTypeIdAndIsAvailableTrue(@Param("roomTypeId") Integer roomTypeId, Pageable pageable);

    @RestResource(path = "by-room-type", rel = "by-room-type")
    Page<Room> findByRoomType_RoomTypeId(@Param("roomTypeId") Integer roomTypeId, Pageable pageable);

    @RestResource(path = "by-amenity", rel = "by-amenity")
    Page<Room> findByAmenities_AmenityId(@Param("amenityId") Integer amenityId, Pageable pageable);

    @RestResource(exported = false)
    boolean existsByRoomNumberAndRoomType_RoomTypeId(Integer roomNumber, Integer roomTypeId);

    @RestResource(path = "by-hotel", rel = "by-hotel")
    Page<Room> findByHotel_HotelId(@Param("hotelId") Integer hotelId, Pageable pageable);

    @RestResource(exported = false)
    long countByHotel_HotelIdAndIsAvailableTrue(Integer hotelId);

    @RestResource(exported = false)
    long countByHotel_HotelId(Integer hotelId);

    @Override
    @RestResource(exported = false)
    <S extends Room> S save(S entity);

    @Override
    @RestResource(exported = false)
    void deleteById(Integer integer);

    @Override
    @RestResource(exported = false)
    void delete(Room entity);

    @Override
    @RestResource(exported = false)
    void deleteAllById(Iterable<? extends Integer> integers);

    @Override
    @RestResource(exported = false)
    void deleteAll(Iterable<? extends Room> entities);

    @Override
    @RestResource(exported = false)
    void deleteAll();
}
