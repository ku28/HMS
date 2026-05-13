package com.hotelmanagement.hotelmanagementbackend.hotel.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.*;
import com.hotelmanagement.hotelmanagementbackend.hotel.service.AmenityService;
import com.hotelmanagement.hotelmanagementbackend.hotel.service.HotelService;
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
@RequestMapping("/api/amenity")
@Tag(name = "Amenity", description = "Amenity Management APIs")
public class AmenityController {

    private final AmenityService amenityService;
    @SuppressWarnings("unused")
    private final HotelService hotelService;

    public AmenityController(AmenityService amenityService, HotelService hotelService) {
        this.amenityService = amenityService;
        this.hotelService = hotelService;
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create a new amenity")
    public ResponseEntity<ApiResponse<AmenityResponseDto>> createAmenity(
            @Valid @RequestBody AmenityRequestDto dto) {
        AmenityResponseDto response = amenityService.createAmenity(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("POSTSUCCESS", "Amenity added successfully", response));
    }

    @GetMapping("/{amenity_id}")
    @Operation(summary = "Get amenity by ID")
    public ResponseEntity<ApiResponse<AmenityResponseDto>> getAmenityById(
            @PathVariable("amenity_id") Integer amenityId) {
        AmenityResponseDto response = amenityService.getAmenityById(amenityId);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Amenity retrieved", response));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all amenities")
    public ResponseEntity<ApiResponse<PagedResponse<AmenityResponseDto>>> getAllAmenities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<AmenityResponseDto> response = amenityService.getAllAmenities(pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Amenities retrieved", response));
    }

    @PutMapping("/update/{amenity_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update amenity details")
    public ResponseEntity<ApiResponse<AmenityResponseDto>> updateAmenity(
            @PathVariable("amenity_id") Integer amenityId,
            @Valid @RequestBody AmenityRequestDto dto) {
        AmenityResponseDto response = amenityService.updateAmenity(amenityId, dto);
        return ResponseEntity.ok(ApiResponse.success("UPDATESUCCESS", "Amenity updated successfully", response));
    }

    @DeleteMapping("/{amenity_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete an amenity")
    public ResponseEntity<ApiResponse<Void>> deleteAmenity(
            @PathVariable("amenity_id") Integer amenityId) {
        amenityService.deleteAmenity(amenityId);
        return ResponseEntity.ok(ApiResponse.success("DELETESUCCESS", "Amenity deleted successfully", null));
    }

    @GetMapping("/hotel/{hotel_id}")
    @Operation(summary = "Get amenities by hotel")
    public ResponseEntity<ApiResponse<PagedResponse<AmenityResponseDto>>> getAmenitiesByHotel(
            @PathVariable("hotel_id") Integer hotelId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<AmenityResponseDto> response = amenityService.getAmenitiesByHotel(hotelId, pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Amenities retrieved", response));
    }

    @GetMapping("/room/{room_id}")
    @Operation(summary = "Get amenities by room")
    public ResponseEntity<ApiResponse<PagedResponse<AmenityResponseDto>>> getAmenitiesByRoom(
            @PathVariable("room_id") Integer roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<AmenityResponseDto> response = amenityService.getAmenitiesByRoom(roomId, pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Amenities retrieved", response));
    }
}
