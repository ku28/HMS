package com.hotelmanagement.hotelmanagementbackend.config;

import com.hotelmanagement.hotelmanagementbackend.security.JwtTokenProvider;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test security configuration that provides mock beans for WebMvcTest slices.
 * Disables CSRF and allows all requests by default for controller unit tests.
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }

    @Bean
    public UserDetailsService testUserDetailsService() {
        return new InMemoryUserDetailsManager(
                User.withUsername("admin@hms.com")
                        .password(passwordEncoder().encode("admin123"))
                        .authorities("ROLE_ADMIN")
                        .build(),
                User.withUsername("user@hms.com")
                        .password(passwordEncoder().encode("user123"))
                        .authorities("ROLE_USER")
                        .build()
        );
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtTokenProvider testJwtTokenProvider() {
        return Mockito.mock(JwtTokenProvider.class);
    }

    @Bean
    public AuthenticationManager testAuthenticationManager() {
        return Mockito.mock(AuthenticationManager.class);
    }
}
