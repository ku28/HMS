package com.hotelmanagement.hotelmanagementbackend.room.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.room.dto.*;
import com.hotelmanagement.hotelmanagementbackend.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Room", description = "Room Management APIs")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/api/rooms/post")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create a new room")
    public ResponseEntity<ApiResponse<RoomResponseDto>> createRoom(
            @Valid @RequestBody RoomRequestDto dto) {
        RoomResponseDto response = roomService.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("POSTSUCCESS", "Room added successfully", response));
    }

    @GetMapping("/api/room/{room_id}")
    @Operation(summary = "Get room by ID")
    public ResponseEntity<ApiResponse<RoomResponseDto>> getRoomById(
            @PathVariable("room_id") Integer roomId) {
        RoomResponseDto response = roomService.getRoomById(roomId);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Room retrieved", response));
    }

    @GetMapping("/api/room/all")
    @Operation(summary = "Get all available rooms")
    public ResponseEntity<ApiResponse<PagedResponse<RoomResponseDto>>> getAllRooms(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<RoomResponseDto> response = roomService.getAllRooms(pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Rooms retrieved", response));
    }

    @GetMapping("/api/rooms/available/{roomTypeId}")
    @Operation(summary = "Get available rooms by type")
    public ResponseEntity<ApiResponse<PagedResponse<RoomResponseDto>>> getAvailableRoomsByType(
            @PathVariable Integer roomTypeId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<RoomResponseDto> response = roomService.getAvailableRoomsByType(roomTypeId, pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Available rooms retrieved", response));
    }

    @GetMapping("/api/room/hotel/{hotel_id}")
    @Operation(summary = "Get rooms by hotel")
    public ResponseEntity<ApiResponse<PagedResponse<RoomResponseDto>>> getRoomsByHotel(
            @PathVariable("hotel_id") Integer hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<RoomResponseDto> response = roomService.getRoomsByHotel(hotelId, pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Rooms retrieved", response));
    }

    @GetMapping("/api/rooms/{amenity_id}")
    @Operation(summary = "Get rooms by amenity")
    public ResponseEntity<ApiResponse<PagedResponse<RoomResponseDto>>> getRoomsByAmenity(
            @PathVariable("amenity_id") Integer amenityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<RoomResponseDto> response = roomService.getRoomsByAmenity(amenityId, pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Rooms retrieved", response));
    }

    @PutMapping("/api/room/update/{room_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update room details")
    public ResponseEntity<ApiResponse<RoomResponseDto>> updateRoom(
            @PathVariable("room_id") Integer roomId,
            @Valid @RequestBody RoomRequestDto dto) {
        RoomResponseDto response = roomService.updateRoom(roomId, dto);
        return ResponseEntity.ok(ApiResponse.success("UPDATESUCCESS", "Room updated successfully", response));
    }

    @DeleteMapping("/api/room/delete/{room_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete a room")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable("room_id") Integer roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success("DELETESUCCESS", "Room deleted successfully", null));
    }
}
