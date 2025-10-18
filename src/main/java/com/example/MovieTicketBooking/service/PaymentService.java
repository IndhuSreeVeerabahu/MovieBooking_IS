package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.entity.Booking;
import com.example.MovieTicketBooking.entity.Payment;
import com.example.MovieTicketBooking.entity.PaymentStatus;
import com.example.MovieTicketBooking.entity.PaymentMethod;
import com.example.MovieTicketBooking.repository.PaymentRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);

    @Value("${cashfree.app.id}")
    private String cashfreeAppId;

    @Value("${cashfree.secret.key}")
    private String cashfreeSecretKey;

    @Value("${cashfree.environment}")
    private String cashfreeEnvironment;

    @Value("${cashfree.api.version}")
    private String cashfreeApiVersion;

    @Value("${cashfree.return.url}")
    private String cashfreeReturnUrl;

    @Value("${cashfree.notify.url}")
    private String cashfreeNotifyUrl;

    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    private String getBaseUrl() {
        return "SANDBOX".equalsIgnoreCase(cashfreeEnvironment) 
            ? "https://sandbox.cashfree.com/pg" 
            : "https://api.cashfree.com/pg";
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-version", cashfreeApiVersion != null ? cashfreeApiVersion : "2023-08-01");
        headers.set("x-client-id", cashfreeAppId);
        headers.set("x-client-secret", cashfreeSecretKey);
        return headers;
    }

    public String createPaymentSession(Booking booking) {
        logger.info("Creating payment session for booking ID: {}", booking.getId());
        logger.info("Booking amount: {} INR", booking.getTotalAmount());
        
        try {
            // Create payment record
            Payment payment = new Payment(booking, booking.getTotalAmount());
            payment.setPaymentMethod(PaymentMethod.CASHFREE);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setCashfreeOrderId("ORDER_" + booking.getId() + "_" + System.currentTimeMillis());
            payment = paymentRepository.save(payment);

            BigDecimal bookingAmount = booking.getTotalAmount();
            BigDecimal maxSandboxAmount = new BigDecimal("1000.00"); // Max ₹1000 for sandbox
            
            if (bookingAmount.compareTo(maxSandboxAmount) > 0) {
                logger.warn("Booking amount {} exceeds sandbox limit {}, using test session", bookingAmount, maxSandboxAmount);
                String testSessionId = "test_session_" + booking.getId() + "_" + System.currentTimeMillis();
                logger.info("Generated test session ID for high amount: {}", testSessionId);
                payment.setPaymentSessionId(testSessionId);
                paymentRepository.save(payment);
                return testSessionId;
            }
            
            int amountInPaise = bookingAmount.multiply(BigDecimal.valueOf(100)).intValue();
            logger.info("Booking amount: {} INR ({} paise)", bookingAmount, amountInPaise);
            
            // Create order request
            Map<String, Object> orderRequest = new HashMap<>();
            orderRequest.put("order_id", payment.getCashfreeOrderId());
            orderRequest.put("order_amount", amountInPaise);
            orderRequest.put("order_currency", "INR");
            
            // Customer details
            Map<String, Object> customerDetails = new HashMap<>();
            customerDetails.put("customer_id", booking.getUser().getId().toString());
            customerDetails.put("customer_name", booking.getUser().getFirstName() + " " + booking.getUser().getLastName());
            customerDetails.put("customer_email", booking.getUser().getEmail());
            customerDetails.put("customer_phone", booking.getUser().getPhoneNumber());
            orderRequest.put("customer_details", customerDetails);
            
            // Order meta
            Map<String, Object> orderMeta = new HashMap<>();
            orderMeta.put("return_url", cashfreeReturnUrl != null ? cashfreeReturnUrl : "http://localhost:8080/payment/success");
            orderMeta.put("notify_url", cashfreeNotifyUrl != null ? cashfreeNotifyUrl : "http://localhost:8080/payment/webhook");
            orderMeta.put("payment_methods", "cc,dc,nb,upi,paylater");
            orderRequest.put("order_meta", orderMeta);
            
            // Make API call to Cashfree sandbox
            String url = getBaseUrl() + "/orders";
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(orderRequest, createHeaders());
            
            logger.info("Making API call to Cashfree sandbox: {}", url);
            logger.info("Request payload: {}", objectMapper.writeValueAsString(orderRequest));
            
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            logger.info("Cashfree API response status: {}", response.getStatusCode());
            logger.info("Cashfree API response body: {}", response.getBody());
            
            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                if (responseJson.has("payment_session_id")) {
                    String paymentSessionId = responseJson.get("payment_session_id").asText();
                    logger.info("Cashfree payment session created successfully: {}", paymentSessionId);
                    payment.setPaymentSessionId(paymentSessionId);
                    paymentRepository.save(payment);
                    return paymentSessionId;
                } else {
                    logger.error("Payment session ID not found in response: {}", response.getBody());
                    throw new RuntimeException("Payment session ID not found in response");
                }
            } else {
                logger.error("Failed to create payment session. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to create payment session: " + response.getStatusCode() + " - " + response.getBody());
            }
            
        } catch (Exception e) {
            logger.error("Error creating payment session with Cashfree API: {}", e.getMessage());
            logger.error("Stack trace: ", e);
            
            logger.warn("Cashfree API failed, using test session");
            String testSessionId = "test_session_" + booking.getId() + "_" + System.currentTimeMillis();
            logger.info("Generated fallback test session ID: {}", testSessionId);
            
            // Update payment record with test session
            Payment payment = paymentRepository.findByBookingId(booking.getId()).orElse(null);
            if (payment != null) {
                payment.setPaymentSessionId(testSessionId);
                paymentRepository.save(payment);
            }
            return testSessionId;
        }
    }

    public boolean verifyPayment(String orderId, String paymentId) {
        logger.info("Verifying payment for order: {}, payment: {}", orderId, paymentId);
        
        // For dummy project - always return true (payment always succeeds)
        logger.info("DUMMY PROJECT: Payment verification always returns SUCCESS");
        return true;
    }

    public Payment getPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId).orElse(null);
    }

    public Payment getPaymentByOrderId(String orderId) {
        return paymentRepository.findByCashfreeOrderId(orderId).orElse(null);
    }

    public String getCashfreeAppId() {
        return cashfreeAppId != null ? cashfreeAppId : "TEST108283821957fe1153788f32479528382801";
    }

    public String getCashfreeEnvironment() {
        return cashfreeEnvironment != null ? cashfreeEnvironment : "SANDBOX";
    }

    public List<Payment> getRecentPayments() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return paymentRepository.findRecentPayments(thirtyDaysAgo);
    }

    public boolean verifyWebhookSignature(String payload, String signature) {
        try {
            // In production, you would verify the webhook signature here
            logger.info("Webhook signature verification: {}", signature);
            return true;
        } catch (Exception e) {
            logger.error("Webhook signature verification failed: {}", e.getMessage());
            return false;
        }
    }

    public String getPaymentStatus(String orderId, String paymentId) {
        try {
            String url = getBaseUrl() + "/orders/" + orderId + "/payments";
            HttpEntity<String> request = new HttpEntity<>(createHeaders());
            
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                JsonNode payments = responseJson.get("data");
                
                if (payments.isArray()) {
                    for (JsonNode payment : payments) {
                        if (paymentId.equals(payment.get("cf_payment_id").asText())) {
                            return payment.get("payment_status").asText();
                        }
                    }
                }
            }
            
            return "UNKNOWN";
        } catch (Exception e) {
            logger.error("Error getting payment status: {}", e.getMessage());
            return "ERROR";
        }
    }
}