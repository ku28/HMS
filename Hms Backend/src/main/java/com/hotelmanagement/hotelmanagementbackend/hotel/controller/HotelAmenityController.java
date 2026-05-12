package com.hotelmanagement.hotelmanagementbackend.hotel.controller;

import com.hotelmanagement.hotelmanagementbackend.common.ApiResponse;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.HotelAmenityRequestDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.service.HotelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hotelamenity")
@Tag(name = "HotelAmenity", description = "Hotel Amenity Association APIs")
public class HotelAmenityController {

    private final HotelService hotelService;

    public HotelAmenityController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @PostMapping("/post")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @Operation(summary = "Associate amenity with hotel")
    public ResponseEntity<ApiResponse<Void>> addAmenityToHotel(
            @Valid @RequestBody HotelAmenityRequestDto dto) {
        hotelService.addAmenityToHotel(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("POSTSUCCESS", "Hotelamenity added successfully", null));
    }
}
