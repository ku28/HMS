package com.hotelmanagement.hotelmanagementbackend.room.repository;

import com.hotelmanagement.hotelmanagementbackend.room.entity.RoomType;
import com.hotelmanagement.hotelmanagementbackend.room.projection.RoomTypeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;

@Repository
@RepositoryRestResource(path = "room-types", collectionResourceRel = "roomTypes",
        excerptProjection = RoomTypeProjection.class)
public interface RoomTypeRepository extends JpaRepository<RoomType, Integer> {

    @RestResource(path = "by-type-name", rel = "by-type-name")
    Page<RoomType> findByTypeNameContainingIgnoreCase(@Param("typeName") String typeName, Pageable pageable);

    @RestResource(exported = false)
    boolean existsByTypeName(String typeName);
}
