package com.hotelmanagement.hotelmanagementbackend.mapper;

import com.hotelmanagement.hotelmanagementbackend.auth.dto.UserResponseDto;
import com.hotelmanagement.hotelmanagementbackend.auth.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        if (user == null) return null;
        return UserResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .role(user.getRole())
                .enabled(user.getEnabled())
                .build();
    }
}
