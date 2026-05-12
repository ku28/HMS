package com.hotelmanagement.hotelmanagementbackend.room.repository;

import com.hotelmanagement.hotelmanagementbackend.room.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    Page<Room> findByIsAvailableTrue(Pageable pageable);

    Page<Room> findByRoomType_RoomTypeIdAndIsAvailableTrue(Integer roomTypeId, Pageable pageable);

    Page<Room> findByRoomType_RoomTypeId(Integer roomTypeId, Pageable pageable);

    Page<Room> findByAmenities_AmenityId(Integer amenityId, Pageable pageable);

    boolean existsByRoomNumberAndRoomType_RoomTypeId(Integer roomNumber, Integer roomTypeId);
}
