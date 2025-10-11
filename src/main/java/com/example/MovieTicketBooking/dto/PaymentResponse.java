package com.example.MovieTicketBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    
    private String paymentSessionId;
    private String paymentUrl;
    private String orderId;
    private String status;
    private String message;
    private String orderToken;
}
