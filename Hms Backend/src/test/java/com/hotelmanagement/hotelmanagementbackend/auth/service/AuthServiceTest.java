package com.hotelmanagement.hotelmanagementbackend.auth.service;

import com.hotelmanagement.hotelmanagementbackend.auth.dto.AuthResponseDto;
import com.hotelmanagement.hotelmanagementbackend.auth.dto.LoginRequestDto;
import com.hotelmanagement.hotelmanagementbackend.auth.dto.RegisterRequestDto;
import com.hotelmanagement.hotelmanagementbackend.auth.entity.User;
import com.hotelmanagement.hotelmanagementbackend.auth.repository.UserRepository;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import com.hotelmanagement.hotelmanagementbackend.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Unit Tests")
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManager authenticationManager;
    @InjectMocks private AuthService authService;

    private RegisterRequestDto registerDto;
    private LoginRequestDto loginDto;

    @BeforeEach
    void setUp() {
        registerDto = RegisterRequestDto.builder().fullName("John Doe")
                .email("john@example.com").password("password123").phone("+1234567890").build();
        loginDto = LoginRequestDto.builder().email("john@example.com").password("password123").build();
    }

    @Test @DisplayName("shouldRegisterUserSuccessfully")
    void shouldRegisterUserSuccessfully() {
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "john@example.com", "encoded", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication auth = mock(Authentication.class);

        when(userRepository.existsByEmail("john@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setUserId(1);
            return u;
        });
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn("jwt-token-123");

        AuthResponseDto result = authService.register(registerDto);
        assertThat(result.getToken()).isEqualTo("jwt-token-123");
        assertThat(result.getEmail()).isEqualTo("john@example.com");
        assertThat(result.getRole()).isEqualTo("ROLE_USER");
        verify(userRepository).save(any(User.class));
    }

    @Test @DisplayName("shouldThrowExceptionWhenEmailAlreadyRegistered")
    void shouldThrowExceptionWhenEmailAlreadyRegistered() {
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);
        assertThatThrownBy(() -> authService.register(registerDto))
                .isInstanceOf(ResourceAlreadyExistsException.class)
                .hasMessageContaining("email");
        verify(userRepository, never()).save(any());
    }

    @Test @DisplayName("shouldLoginSuccessfully")
    void shouldLoginSuccessfully() {
        User user = User.builder().userId(1).email("john@example.com").fullName("John Doe")
                .role("ROLE_USER").enabled(true).build();
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(
                "john@example.com", "encoded", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication auth = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(auth);
        when(auth.getPrincipal()).thenReturn(userDetails);
        when(jwtTokenProvider.generateToken(userDetails)).thenReturn("jwt-token-456");
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(user));

        AuthResponseDto result = authService.login(loginDto);
        assertThat(result.getToken()).isEqualTo("jwt-token-456");
        assertThat(result.getFullName()).isEqualTo("John Doe");
    }

    @Test @DisplayName("shouldThrowExceptionForInvalidCredentials")
    void shouldThrowExceptionForInvalidCredentials() {
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        assertThatThrownBy(() -> authService.login(loginDto))
                .isInstanceOf(BadCredentialsException.class);
    }
}
