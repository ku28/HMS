package com.hotelmanagement.hotelmanagementbackend.auth.repository;

import com.hotelmanagement.hotelmanagementbackend.auth.entity.User;
import com.hotelmanagement.hotelmanagementbackend.repository.RepositoryDataJpaTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryDataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should save user successfully")
    void shouldSaveUserSuccessfully() {

        User user = User.builder()
                .email("test@gmail.com")
                .password("password123")
                .fullName("Test User")
                .role("ROLE_USER")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        User savedUser = userRepository.save(user);

        assertThat(savedUser).isNotNull();
        assertThat(savedUser.getUserId()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should find user by email")
    void shouldFindUserByEmail() {

        User user = User.builder()
                .email("admin@gmail.com")
                .password("password123")
                .fullName("Admin")
                .role("ROLE_ADMIN")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        Optional<User> foundUser =
                userRepository.findByEmail("admin@gmail.com");

        assertThat(foundUser).isPresent();
    }

    @Test
    @DisplayName("Should check email exists")
    void shouldCheckEmailExists() {

        User user = User.builder()
                .email("exists@gmail.com")
                .password("password123")
                .fullName("Exists User")
                .role("ROLE_USER")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        boolean exists =
                userRepository.existsByEmail("exists@gmail.com");

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should find users by role")
    void shouldFindUsersByRole() {

        User admin = User.builder()
                .email("roleadmin@gmail.com")
                .password("password123")
                .fullName("Role Admin")
                .role("ROLE_ADMIN")
                .enabled(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(admin);

        Page<User> result =
                userRepository.findByRole(
                        "ROLE_ADMIN",
                        PageRequest.of(0, 5)
                );

        assertThat(result.getContent()).isNotEmpty();
    }
}
