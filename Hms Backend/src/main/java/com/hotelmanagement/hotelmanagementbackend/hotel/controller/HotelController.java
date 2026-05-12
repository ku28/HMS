package com.hotelmanagement.hotelmanagementbackend.hotel.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.*;
import com.hotelmanagement.hotelmanagementbackend.hotel.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotels")
@Tag(name = "Hotel", description = "Hotel Management APIs")
public class HotelController {

    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Create a new hotel")
    public ResponseEntity<ApiResponse<HotelResponseDto>> createHotel(
            @Valid @RequestBody HotelRequestDto dto) {
        HotelResponseDto response = hotelService.createHotel(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("POSTSUCCESS", "Hotel added successfully", response));
    }

    @GetMapping("/{hotel_id}")
    @Operation(summary = "Get hotel by ID")
    public ResponseEntity<ApiResponse<HotelResponseDto>> getHotelById(
            @PathVariable("hotel_id") Integer hotelId) {
        HotelResponseDto response = hotelService.getHotelById(hotelId);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Hotel retrieved", response));
    }

    @GetMapping("/all")
    @Operation(summary = "Get all hotels with pagination")
    public ResponseEntity<ApiResponse<PagedResponse<HotelResponseDto>>> getAllHotels(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "hotelId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        PagedResponse<HotelResponseDto> response = hotelService.searchHotels(search, pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Hotels retrieved", response));
    }

    @PutMapping("/update/{hotel_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Update hotel details")
    public ResponseEntity<ApiResponse<HotelResponseDto>> updateHotel(
            @PathVariable("hotel_id") Integer hotelId,
            @Valid @RequestBody HotelRequestDto dto) {
        HotelResponseDto response = hotelService.updateHotel(hotelId, dto);
        return ResponseEntity.ok(ApiResponse.success("UPDATESUCCESS", "Hotel updated successfully", response));
    }

    @DeleteMapping("/delete/{hotel_id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Delete a hotel")
    public ResponseEntity<ApiResponse<Void>> deleteHotel(
            @PathVariable("hotel_id") Integer hotelId) {
        hotelService.deleteHotel(hotelId);
        return ResponseEntity.ok(ApiResponse.success("DELETESUCCESS", "Hotel deleted successfully", null));
    }

    @GetMapping("/amenity/{amenity_id}")
    @Operation(summary = "Get hotels by amenity")
    public ResponseEntity<ApiResponse<PagedResponse<HotelResponseDto>>> getHotelsByAmenity(
            @PathVariable("amenity_id") Integer amenityId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<HotelResponseDto> response = hotelService.getHotelsByAmenity(amenityId, pageable);
        return ResponseEntity.ok(ApiResponse.success("SUCCESS", "Hotels retrieved", response));
    }
}
