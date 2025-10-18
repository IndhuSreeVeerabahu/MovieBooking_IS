package com.example.MovieTicketBooking.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Web Configuration for Spring Data Web Support
 * This configuration enables proper pagination and sorting support
 * and fixes the PageImpl serialization warning
 */
@Configuration
@EnableSpringDataWebSupport
public class WebConfig {
    // Configuration is handled by the @EnableSpringDataWebSupport annotation
}
