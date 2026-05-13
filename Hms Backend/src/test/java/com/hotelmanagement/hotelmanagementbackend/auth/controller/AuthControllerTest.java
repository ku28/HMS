package com.hotelmanagement.hotelmanagementbackend.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelmanagement.hotelmanagementbackend.auth.dto.AuthResponseDto;
import com.hotelmanagement.hotelmanagementbackend.auth.dto.LoginRequestDto;
import com.hotelmanagement.hotelmanagementbackend.auth.dto.RegisterRequestDto;
import com.hotelmanagement.hotelmanagementbackend.auth.service.AuthService;
import com.hotelmanagement.hotelmanagementbackend.config.TestSecurityConfig;
import com.hotelmanagement.hotelmanagementbackend.exception.GlobalExceptionHandler;
import com.hotelmanagement.hotelmanagementbackend.exception.ResourceAlreadyExistsException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@Import({TestSecurityConfig.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
@DisplayName("AuthController Integration Tests")
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @MockitoBean private AuthService authService;

    @Test @DisplayName("shouldRegisterUserAndReturnCreated")
    void shouldRegisterUserAndReturnCreated() throws Exception {
        RegisterRequestDto dto = RegisterRequestDto.builder().fullName("John Doe")
                .email("john@example.com").password("password123").phone("+1234567890").build();
        AuthResponseDto response = AuthResponseDto.builder().token("jwt-token")
                .email("john@example.com").fullName("John Doe").role("ROLE_USER").build();
        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("POSTSUCCESS"))
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.email").value("john@example.com"));
    }

    @Test @DisplayName("shouldReturnBadRequestForBlankEmail")
    void shouldReturnBadRequestForBlankEmail() throws Exception {
        RegisterRequestDto dto = RegisterRequestDto.builder().fullName("John")
                .email("").password("password123").build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForInvalidEmail")
    void shouldReturnBadRequestForInvalidEmail() throws Exception {
        RegisterRequestDto dto = RegisterRequestDto.builder().fullName("John")
                .email("not-an-email").password("password123").build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldReturnBadRequestForShortPassword")
    void shouldReturnBadRequestForShortPassword() throws Exception {
        RegisterRequestDto dto = RegisterRequestDto.builder().fullName("John")
                .email("john@example.com").password("12345").build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test @DisplayName("shouldLoginSuccessfullyAndReturnToken")
    void shouldLoginSuccessfullyAndReturnToken() throws Exception {
        LoginRequestDto dto = LoginRequestDto.builder().email("john@example.com").password("password123").build();
        AuthResponseDto response = AuthResponseDto.builder().token("jwt-token")
                .email("john@example.com").fullName("John Doe").role("ROLE_USER").build();
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("LOGINSUCCESS"))
                .andExpect(jsonPath("$.data.token").value("jwt-token"));
    }

    @Test @DisplayName("shouldReturnUnauthorizedForInvalidCredentials")
    void shouldReturnUnauthorizedForInvalidCredentials() throws Exception {
        LoginRequestDto dto = LoginRequestDto.builder().email("john@example.com").password("wrong").build();
        when(authService.login(any())).thenThrow(new BadCredentialsException("Bad credentials"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("AUTHFAILS"));
    }

    @Test @DisplayName("shouldReturnConflictForDuplicateEmail")
    void shouldReturnConflictForDuplicateEmail() throws Exception {
        RegisterRequestDto dto = RegisterRequestDto.builder().fullName("John")
                .email("existing@example.com").password("password123").build();
        when(authService.register(any())).thenThrow(
                new ResourceAlreadyExistsException("User", "email", "existing@example.com"));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code").value("ADDFAILS"));
    }

    @Test @DisplayName("shouldReturnBadRequestForMissingFullName")
    void shouldReturnBadRequestForMissingFullName() throws Exception {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .email("john@example.com").password("password123").build();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }
}
