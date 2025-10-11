package com.example.MovieTicketBooking.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cashfree")
@Data
public class CashfreeConfig {
    
    private String appId;
    private String secretKey;
    private String environment = "TEST"; // TEST or PRODUCTION
    private String baseUrl = "https://sandbox.cashfree.com"; // For TEST environment
    
    public String getBaseUrl() {
        if ("PRODUCTION".equalsIgnoreCase(environment)) {
            return "https://api.cashfree.com";
        }
        return "https://sandbox.cashfree.com";
    }
}
