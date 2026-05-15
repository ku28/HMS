package com.hotelmanagement.hotelmanagementbackend.room.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.room.dto.RoomAmenityRequestDto;
import com.hotelmanagement.hotelmanagementbackend.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room-management/room-amenities")
@Tag(name = "RoomAmenity", description = "Room Amenity Association APIs")
public class RoomAmenityController {

    private final RoomService roomService;

    public RoomAmenityController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Associate amenity with room")
    public ResponseEntity<ApiResponse<Void>> addAmenityToRoom(
            @Valid @RequestBody RoomAmenityRequestDto dto) {
        roomService.addAmenityToRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("POSTSUCCESS", "RoomAmenity added successfully", null));
    }
}
