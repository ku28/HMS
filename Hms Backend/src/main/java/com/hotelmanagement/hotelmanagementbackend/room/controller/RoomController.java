package com.hotelmanagement.hotelmanagementbackend.room.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.room.dto.*;
import com.hotelmanagement.hotelmanagementbackend.room.service.RoomService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/room-management/rooms")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
@Tag(name = "Room", description = "Room Management APIs")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping
    @Operation(summary = "Create a new room")
    public ResponseEntity<ApiResponse<RoomResponseDto>> createRoom(
            @Valid @RequestBody RoomRequestDto dto) {
        RoomResponseDto response = roomService.createRoom(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("POSTSUCCESS", "Room added successfully", response));
    }

    @PutMapping("/{room_id}")
    @Operation(summary = "Update room details")
    public ResponseEntity<ApiResponse<RoomResponseDto>> updateRoom(
            @PathVariable("room_id") Integer roomId,
            @Valid @RequestBody RoomRequestDto dto) {
        RoomResponseDto response = roomService.updateRoom(roomId, dto);
        return ResponseEntity.ok(ApiResponse.success("UPDATESUCCESS", "Room updated successfully", response));
    }

    @DeleteMapping("/{room_id}")
    @Operation(summary = "Delete a room")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(
            @PathVariable("room_id") Integer roomId) {
        roomService.deleteRoom(roomId);
        return ResponseEntity.ok(ApiResponse.success("DELETESUCCESS", "Room deleted successfully", null));
    }
}
