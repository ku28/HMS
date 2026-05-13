package com.hotelmanagement.hotelmanagementbackend.hotel.service;

import com.hotelmanagement.hotelmanagementbackend.common.PagedResponse;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceNotFoundException;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.HotelAmenityRequestDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.HotelRequestDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.dto.HotelResponseDto;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Amenity;
import com.hotelmanagement.hotelmanagementbackend.hotel.entity.Hotel;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.AmenityRepository;
import com.hotelmanagement.hotelmanagementbackend.hotel.repository.HotelRepository;
import com.hotelmanagement.hotelmanagementbackend.mapper.HotelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("HotelService Unit Tests")
class HotelServiceImplTest {

    @Mock
    private HotelRepository hotelRepository;

    @Mock
    private AmenityRepository amenityRepository;

    @Mock
    private HotelMapper hotelMapper;

    @InjectMocks
    private HotelServiceImpl hotelService;

    private Hotel testHotel;
    private HotelRequestDto testRequestDto;
    private HotelResponseDto testResponseDto;

    @BeforeEach
    void setUp() {
        testHotel = Hotel.builder()
                .hotelId(1)
                .name("Grand Plaza Hotel")
                .location("New York, NY")
                .description("A luxury 5-star hotel in Manhattan")
                .amenities(new HashSet<>())
                .build();

        testRequestDto = HotelRequestDto.builder()
                .name("Grand Plaza Hotel")
                .location("New York, NY")
                .description("A luxury 5-star hotel in Manhattan")
                .build();

        testResponseDto = HotelResponseDto.builder()
                .hotelId(1)
                .name("Grand Plaza Hotel")
                .location("New York, NY")
                .description("A luxury 5-star hotel in Manhattan")
                .amenities(Collections.emptyList())
                .build();
    }

    @Nested
    @DisplayName("Create Hotel Tests")
    class CreateHotelTests {

        @Test
        @DisplayName("shouldCreateHotelSuccessfully")
        void shouldCreateHotelSuccessfully() {
            when(hotelRepository.existsByNameAndLocation(testRequestDto.getName(), testRequestDto.getLocation()))
                    .thenReturn(false);
            when(hotelMapper.toEntity(testRequestDto)).thenReturn(testHotel);
            when(hotelRepository.save(testHotel)).thenReturn(testHotel);
            when(hotelMapper.toResponseDtoWithoutAmenities(testHotel)).thenReturn(testResponseDto);

            HotelResponseDto result = hotelService.createHotel(testRequestDto);

            assertThat(result).isNotNull();
            assertThat(result.getHotelId()).isEqualTo(1);
            assertThat(result.getName()).isEqualTo("Grand Plaza Hotel");
            assertThat(result.getLocation()).isEqualTo("New York, NY");
            verify(hotelRepository).save(any(Hotel.class));
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenHotelAlreadyExists")
        void shouldThrowExceptionWhenHotelAlreadyExists() {
            when(hotelRepository.existsByNameAndLocation(testRequestDto.getName(), testRequestDto.getLocation()))
                    .thenReturn(true);

            assertThatThrownBy(() -> hotelService.createHotel(testRequestDto))
                    .isInstanceOf(ResourceAlreadyExistsException.class)
                    .hasMessageContaining("Hotel already exists");

            verify(hotelRepository, never()).save(any(Hotel.class));
        }
    }

    @Nested
    @DisplayName("Get Hotel Tests")
    class GetHotelTests {

        @Test
        @DisplayName("shouldReturnHotelById")
        void shouldReturnHotelById() {
            when(hotelRepository.findById(1)).thenReturn(Optional.of(testHotel));
            when(hotelMapper.toResponseDto(testHotel)).thenReturn(testResponseDto);

            HotelResponseDto result = hotelService.getHotelById(1);

            assertThat(result).isNotNull();
            assertThat(result.getHotelId()).isEqualTo(1);
            assertThat(result.getName()).isEqualTo("Grand Plaza Hotel");
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenHotelNotFound")
        void shouldThrowExceptionWhenHotelNotFound() {
            when(hotelRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> hotelService.getHotelById(999))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Hotel not found");
        }
    }

    @Nested
    @DisplayName("Search Hotels Tests")
    class SearchHotelsTests {

        @Test
        @DisplayName("shouldReturnPaginatedHotelsWithSearchQuery")
        void shouldReturnPaginatedHotelsWithSearchQuery() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Hotel> hotelPage = new PageImpl<>(List.of(testHotel), pageable, 1);

            when(hotelRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                    "Grand", "Grand", pageable)).thenReturn(hotelPage);
            when(hotelMapper.toResponseDtoWithoutAmenities(testHotel)).thenReturn(testResponseDto);

            PagedResponse<HotelResponseDto> result = hotelService.searchHotels("Grand", pageable);

            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getPageNumber()).isZero();
        }

        @Test
        @DisplayName("shouldReturnEmptyPageWhenNoHotelsMatchSearch")
        void shouldReturnEmptyPageWhenNoHotelsMatchSearch() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Hotel> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(hotelRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                    "NonExistent", "NonExistent", pageable)).thenReturn(emptyPage);

            PagedResponse<HotelResponseDto> result = hotelService.searchHotels("NonExistent", pageable);

            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("shouldReturnAllHotelsWhenSearchIsBlank")
        void shouldReturnAllHotelsWhenSearchIsBlank() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Hotel> hotelPage = new PageImpl<>(List.of(testHotel), pageable, 1);

            when(hotelRepository.findByNameContainingIgnoreCaseOrLocationContainingIgnoreCase(
                    "", "", pageable)).thenReturn(hotelPage);
            when(hotelMapper.toResponseDtoWithoutAmenities(testHotel)).thenReturn(testResponseDto);

            PagedResponse<HotelResponseDto> result = hotelService.searchHotels("", pageable);

            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("Update Hotel Tests")
    class UpdateHotelTests {

        @Test
        @DisplayName("shouldUpdateHotelSuccessfully")
        void shouldUpdateHotelSuccessfully() {
            HotelRequestDto updateDto = HotelRequestDto.builder()
                    .name("Updated Hotel Name")
                    .location("Los Angeles, CA")
                    .description("Updated description")
                    .build();

            HotelResponseDto updatedResponse = HotelResponseDto.builder()
                    .hotelId(1)
                    .name("Updated Hotel Name")
                    .location("Los Angeles, CA")
                    .description("Updated description")
                    .amenities(Collections.emptyList())
                    .build();

            when(hotelRepository.findById(1)).thenReturn(Optional.of(testHotel));
            when(hotelRepository.save(testHotel)).thenReturn(testHotel);
            when(hotelMapper.toResponseDtoWithoutAmenities(testHotel)).thenReturn(updatedResponse);

            HotelResponseDto result = hotelService.updateHotel(1, updateDto);

            assertThat(result.getName()).isEqualTo("Updated Hotel Name");
            assertThat(result.getLocation()).isEqualTo("Los Angeles, CA");
            verify(hotelMapper).updateEntity(testHotel, updateDto);
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenUpdatingNonExistentHotel")
        void shouldThrowExceptionWhenUpdatingNonExistentHotel() {
            when(hotelRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> hotelService.updateHotel(999, testRequestDto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("Delete Hotel Tests")
    class DeleteHotelTests {

        @Test
        @DisplayName("shouldDeleteHotelSuccessfully")
        void shouldDeleteHotelSuccessfully() {
            when(hotelRepository.existsById(1)).thenReturn(true);

            hotelService.deleteHotel(1);

            verify(hotelRepository).deleteById(1);
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenDeletingNonExistentHotel")
        void shouldThrowExceptionWhenDeletingNonExistentHotel() {
            when(hotelRepository.existsById(999)).thenReturn(false);

            assertThatThrownBy(() -> hotelService.deleteHotel(999))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(hotelRepository, never()).deleteById(anyInt());
        }
    }

    @Nested
    @DisplayName("Hotel Amenity Tests")
    class HotelAmenityTests {

        @Test
        @DisplayName("shouldAddAmenityToHotelSuccessfully")
        void shouldAddAmenityToHotelSuccessfully() {
            Amenity amenity = Amenity.builder()
                    .amenityId(1)
                    .name("Swimming Pool")
                    .description("Olympic size pool")
                    .build();

            HotelAmenityRequestDto requestDto = new HotelAmenityRequestDto();
            requestDto.setHotelId(1);
            requestDto.setAmenityId(1);

            when(hotelRepository.findById(1)).thenReturn(Optional.of(testHotel));
            when(amenityRepository.findById(1)).thenReturn(Optional.of(amenity));
            when(hotelRepository.save(testHotel)).thenReturn(testHotel);

            hotelService.addAmenityToHotel(requestDto);

            assertThat(testHotel.getAmenities()).contains(amenity);
            verify(hotelRepository).save(testHotel);
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenHotelNotFoundForAmenity")
        void shouldThrowExceptionWhenHotelNotFoundForAmenity() {
            HotelAmenityRequestDto requestDto = new HotelAmenityRequestDto();
            requestDto.setHotelId(999);
            requestDto.setAmenityId(1);

            when(hotelRepository.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> hotelService.addAmenityToHotel(requestDto))
                    .isInstanceOf(ResourceNotFoundException.class);
        }

        @Test
        @DisplayName("shouldGetHotelsByAmenitySuccessfully")
        void shouldGetHotelsByAmenitySuccessfully() {
            Pageable pageable = PageRequest.of(0, 10);
            Page<Hotel> hotelPage = new PageImpl<>(List.of(testHotel), pageable, 1);

            when(amenityRepository.existsById(1)).thenReturn(true);
            when(hotelRepository.findByAmenities_AmenityId(1, pageable)).thenReturn(hotelPage);
            when(hotelMapper.toResponseDtoWithoutAmenities(testHotel)).thenReturn(testResponseDto);

            PagedResponse<HotelResponseDto> result = hotelService.getHotelsByAmenity(1, pageable);

            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("shouldThrowExceptionWhenAmenityNotFoundForSearch")
        void shouldThrowExceptionWhenAmenityNotFoundForSearch() {
            Pageable pageable = PageRequest.of(0, 10);
            when(amenityRepository.existsById(999)).thenReturn(false);

            assertThatThrownBy(() -> hotelService.getHotelsByAmenity(999, pageable))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }
}
