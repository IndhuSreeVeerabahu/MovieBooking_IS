package com.example.MovieTicketBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCallbackRequest {
    
    private String orderId;
    private String orderToken;
    private String paymentStatus;
    private String paymentMessage;
    private String referenceId;
    private String txStatus;
    private String txMsg;
    private String txTime;
    private String signature;
}
