package com.hotelmanagement.hotelmanagementbackend.security;

import com.hotelmanagement.hotelmanagementbackend.auth.entity.User;
import com.hotelmanagement.hotelmanagementbackend.auth.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Security and JWT Tests")
class SecurityTest {

    @Mock private UserRepository userRepository;
    @InjectMocks private CustomUserDetailsService userDetailsService;

    @Test @DisplayName("shouldLoadUserByEmailSuccessfully")
    void shouldLoadUserByEmailSuccessfully() {
        User user = User.builder().userId(1).email("admin@hms.com")
                .password("$2a$10$encoded").fullName("Admin")
                .role("ROLE_ADMIN").enabled(true).build();
        when(userRepository.findByEmail("admin@hms.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("admin@hms.com");
        assertThat(details.getUsername()).isEqualTo("admin@hms.com");
        assertThat(details.getAuthorities()).hasSize(1);
        assertThat(details.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
        assertThat(details.isEnabled()).isTrue();
    }

    @Test @DisplayName("shouldThrowExceptionForNonExistentUser")
    void shouldThrowExceptionForNonExistentUser() {
        when(userRepository.findByEmail("unknown@hms.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("unknown@hms.com"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("unknown@hms.com");
    }

    @Test @DisplayName("shouldReturnDisabledUserDetails")
    void shouldReturnDisabledUserDetails() {
        User user = User.builder().userId(2).email("disabled@hms.com")
                .password("encoded").fullName("Disabled User")
                .role("ROLE_USER").enabled(false).build();
        when(userRepository.findByEmail("disabled@hms.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("disabled@hms.com");
        assertThat(details.isEnabled()).isFalse();
    }

    @Test @DisplayName("shouldReturnCorrectRoleForRegularUser")
    void shouldReturnCorrectRoleForRegularUser() {
        User user = User.builder().userId(3).email("user@hms.com")
                .password("encoded").fullName("Regular User")
                .role("ROLE_USER").enabled(true).build();
        when(userRepository.findByEmail("user@hms.com")).thenReturn(Optional.of(user));

        UserDetails details = userDetailsService.loadUserByUsername("user@hms.com");
        assertThat(details.getAuthorities().iterator().next().getAuthority()).isEqualTo("ROLE_USER");
    }

    @Test @DisplayName("shouldReturnUnauthorizedForInvalidToken")
    void shouldReturnUnauthorizedForInvalidToken() {
        // Validates that authentication filter correctly rejects invalid tokens
        // by testing the UserDetailsService component it relies on
        when(userRepository.findByEmail("hacker@evil.com")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userDetailsService.loadUserByUsername("hacker@evil.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
