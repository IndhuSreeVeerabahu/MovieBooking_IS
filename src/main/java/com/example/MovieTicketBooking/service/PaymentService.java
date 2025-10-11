package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.entity.Booking;
import com.example.MovieTicketBooking.entity.Payment;
import com.example.MovieTicketBooking.entity.PaymentStatus;
import com.example.MovieTicketBooking.entity.PaymentMethod;
import com.example.MovieTicketBooking.repository.PaymentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentService.class);
    
    private final PaymentRepository paymentRepository;
    private final RestTemplate restTemplate;
    
    @Value("${cashfree.app.id}")
    private String cashfreeAppId;
    
    @Value("${cashfree.secret.key}")
    private String cashfreeSecretKey;
    
    @Value("${cashfree.environment}")
    private String cashfreeEnvironment;
    
    @Value("${cashfree.api.version}")
    private String cashfreeApiVersion;
    
    @Value("${cashfree.return.url}")
    private String returnUrl;
    
    @Value("${cashfree.notify.url}")
    private String notifyUrl;
    
    private static final String CASHFREE_BASE_URL_SANDBOX = "https://sandbox.cashfree.com/pg";
    private static final String CASHFREE_BASE_URL_PRODUCTION = "https://api.cashfree.com/pg";
    
    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
        this.restTemplate = new RestTemplate();
    }
    
    /**
     * Create a payment session with Cashfree
     */
    public String createPaymentSession(Booking booking) {
        try {
            logger.info("Creating payment session for booking ID: {}", booking.getId());
            
            // Create payment record
            Payment payment = new Payment(booking, booking.getTotalAmount());
            payment.setPaymentMethod(PaymentMethod.CASHFREE);
            payment.setPaymentStatus(PaymentStatus.PENDING);
            payment.setCashfreeOrderId("ORDER_" + booking.getId() + "_" + System.currentTimeMillis());
            
            // Save payment record
            payment = paymentRepository.save(payment);
            
            // Prepare Cashfree order request
            Map<String, Object> orderRequest = new HashMap<>();
            orderRequest.put("order_id", payment.getCashfreeOrderId());
            orderRequest.put("order_amount", booking.getTotalAmount());
            orderRequest.put("order_currency", "INR");
            orderRequest.put("customer_details", createCustomerDetails(booking));
            orderRequest.put("order_meta", createOrderMeta(booking));
            orderRequest.put("order_note", "Movie Ticket Booking - " + booking.getShow().getMovie().getTitle());
            orderRequest.put("order_tags", Map.of("booking_id", booking.getId().toString()));
            
            // For college project, we'll simulate the session creation
            // In production, you would make actual API call to Cashfree
            String sessionId = "session_" + UUID.randomUUID().toString().replace("-", "");
            payment.setPaymentSessionId(sessionId);
            paymentRepository.save(payment);
            
            logger.info("Payment session created successfully: {}", sessionId);
            return sessionId;
            
        } catch (Exception e) {
            logger.error("Error creating payment session for booking {}: {}", booking.getId(), e.getMessage(), e);
            throw new RuntimeException("Failed to create payment session", e);
        }
    }
    
    /**
     * Verify payment with Cashfree
     */
    public boolean verifyPayment(String orderId, String paymentId) {
        try {
            logger.info("Verifying payment - Order ID: {}, Payment ID: {}", orderId, paymentId);
            
            // Find payment record
            Payment payment = paymentRepository.findByCashfreeOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment not found for order: " + orderId));
            
            // For college project, simulate payment verification
            // In production, you would make API call to Cashfree to verify payment
            boolean isPaymentValid = simulatePaymentVerification(paymentId);
            
            if (isPaymentValid) {
                payment.setPaymentStatus(PaymentStatus.COMPLETED);
                payment.setCashfreePaymentId(paymentId);
                payment.setPaidAt(LocalDateTime.now());
                payment.setGatewayResponse("Payment verified successfully");
                paymentRepository.save(payment);
                
                // Update booking status
                updateBookingPaymentStatus(payment.getBooking(), true);
                
                logger.info("Payment verified successfully for order: {}", orderId);
                return true;
            } else {
                payment.setPaymentStatus(PaymentStatus.FAILED);
                payment.setFailureReason("Payment verification failed");
                paymentRepository.save(payment);
                
                // Update booking status
                updateBookingPaymentStatus(payment.getBooking(), false);
                
                logger.warn("Payment verification failed for order: {}", orderId);
                return false;
            }
            
        } catch (Exception e) {
            logger.error("Error verifying payment for order {}: {}", orderId, e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Get payment by booking ID
     */
    public Payment getPaymentByBookingId(Long bookingId) {
        return paymentRepository.findByBookingId(bookingId)
            .orElse(null);
    }
    
    /**
     * Get Cashfree App ID
     */
    public String getCashfreeAppId() {
        return cashfreeAppId;
    }
    
    /**
     * Get Cashfree Environment
     */
    public String getCashfreeEnvironment() {
        return cashfreeEnvironment;
    }
    
    /**
     * Get recent payments (last 30 days)
     */
    public List<Payment> getRecentPayments() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        return paymentRepository.findRecentPayments(thirtyDaysAgo);
    }
    
    /**
     * Verify webhook signature (for production use)
     */
    public boolean verifyWebhookSignature(String payload, String signature) {
        // For college project, we'll skip signature verification
        // In production, you would implement proper signature verification
        logger.info("Webhook signature verification skipped for college project");
        return true;
    }
    
    /**
     * Create customer details for Cashfree order
     */
    private Map<String, Object> createCustomerDetails(Booking booking) {
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("customer_id", booking.getUser().getId().toString());
        customerDetails.put("customer_name", booking.getUser().getFirstName() + " " + booking.getUser().getLastName());
        customerDetails.put("customer_email", booking.getUser().getEmail());
        customerDetails.put("customer_phone", booking.getUser().getPhoneNumber());
        return customerDetails;
    }
    
    /**
     * Create order metadata for Cashfree order
     */
    private Map<String, Object> createOrderMeta(Booking booking) {
        Map<String, Object> orderMeta = new HashMap<>();
        orderMeta.put("return_url", returnUrl);
        orderMeta.put("notify_url", notifyUrl);
        orderMeta.put("payment_methods", "cc,dc,nb,upi,wallet");
        orderMeta.put("movie_title", booking.getShow().getMovie().getTitle());
        orderMeta.put("theater_name", booking.getShow().getTheater().getName());
        orderMeta.put("show_date", booking.getShow().getShowDate().toString());
        orderMeta.put("show_time", booking.getShow().getShowTime().toString());
        return orderMeta;
    }
    
    /**
     * Simulate payment verification (for college project)
     */
    private boolean simulatePaymentVerification(String paymentId) {
        // For college project, simulate successful payment verification
        // In production, this would be replaced with actual Cashfree API call
        logger.info("Simulating payment verification for payment ID: {}", paymentId);
        
        // Simulate some payment IDs as failed for testing
        if (paymentId.contains("fail") || paymentId.contains("error")) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Update booking payment status
     */
    private void updateBookingPaymentStatus(Booking booking, boolean paymentSuccessful) {
        try {
            if (paymentSuccessful) {
                booking.setPaymentStatus(Booking.PaymentStatus.COMPLETED);
                booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);
            } else {
                booking.setPaymentStatus(Booking.PaymentStatus.FAILED);
                booking.setBookingStatus(Booking.BookingStatus.PENDING);
            }
            // Note: In a real implementation, you would inject BookingService and update the booking
            logger.info("Booking payment status updated for booking ID: {}", booking.getId());
        } catch (Exception e) {
            logger.error("Error updating booking payment status: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Get Cashfree base URL based on environment
     */
    private String getCashfreeBaseUrl() {
        return "SANDBOX".equalsIgnoreCase(cashfreeEnvironment) || "TEST".equalsIgnoreCase(cashfreeEnvironment)
            ? CASHFREE_BASE_URL_SANDBOX
            : CASHFREE_BASE_URL_PRODUCTION;
    }
    
    /**
     * Create HTTP headers for Cashfree API calls
     */
    private HttpHeaders createCashfreeHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-version", cashfreeApiVersion);
        headers.set("x-client-id", cashfreeAppId);
        headers.set("x-client-secret", cashfreeSecretKey);
        return headers;
    }
}