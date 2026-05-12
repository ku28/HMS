package com.hotelmanagement.hotelmanagementbackend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

/**
 * Shared MockMvc configuration for controller integration tests.
 */
@TestConfiguration
public class TestMockMvcConfig {

    @Bean
    public MockMvc mockMvc(WebApplicationContext context) {
        return MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }
}
