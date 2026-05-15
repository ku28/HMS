package com.hotelmanagement.hotelmanagementbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.data.web.config.SortHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PaginationConfig {

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
        return resolver -> {
            resolver.setFallbackPageable(PageRequest.of(0, 20));
            resolver.setMaxPageSize(100);
            resolver.setPageParameterName("page");
            resolver.setSizeParameterName("size");
        };
    }

    @Bean
    public SortHandlerMethodArgumentResolverCustomizer sortCustomizer() {
        return resolver -> resolver.setSortParameter("sort");
    }
}
