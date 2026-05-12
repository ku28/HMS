package com.hotelmanagement.hotelmanagementbackend.room.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.room.dto.*;
import com.hotelmanagement.hotelmanagementbackend.room.service.RoomTypeService;
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
@RequestMapping("/api/RoomType")
@Tag(name = "RoomType", description = "Room Type Management APIs")
public class RoomTypeController {

    private final RoomTypeService roomTypeService;

    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create a new room type")
    public ResponseEntity<ApiResponse<RoomTypeResponseDto>> createRoomType(
            @Valid @RequestBody RoomTypeRequestDto dto) {
        RoomTypeResponseDto response = roomTypeService.createRoomType(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("POSTSUCCESS", "RoomType added successfully", response));
    }

    @GetMapping("/{RoomType_id}")
    @Operation(summary = "Get room type by ID")
    public ResponseEntity<ApiResponse<RoomTypeResponseDto>> getRoomTypeById(
            @PathVariable("RoomType_id") Integer roomTypeId) {
        RoomTypeResponseDto response = roomTypeService.getRoomTypeById(roomTypeId);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "RoomType retrieved", response));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all room types")
    public ResponseEntity<ApiResponse<PagedResponse<RoomTypeResponseDto>>> getAllRoomTypes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<RoomTypeResponseDto> response = roomTypeService.getAllRoomTypes(pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Room types retrieved", response));
    }

    @PutMapping("/update/{RoomType_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update room type details")
    public ResponseEntity<ApiResponse<RoomTypeResponseDto>> updateRoomType(
            @PathVariable("RoomType_id") Integer roomTypeId,
            @Valid @RequestBody RoomTypeRequestDto dto) {
        RoomTypeResponseDto response = roomTypeService.updateRoomType(roomTypeId, dto);
        return ResponseEntity.ok(ApiResponse.success("UPDATESUCCESS", "RoomType updated successfully", response));
    }

    @DeleteMapping("/delete/{RoomType_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete a room type")
    public ResponseEntity<ApiResponse<Void>> deleteRoomType(
            @PathVariable("RoomType_id") Integer roomTypeId) {
        roomTypeService.deleteRoomType(roomTypeId);
        return ResponseEntity.ok(ApiResponse.success("DELETESUCCESS", "RoomType deleted successfully", null));
    }
}
