package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.entity.Booking;
import com.example.MovieTicketBooking.entity.Payment;
import com.example.MovieTicketBooking.entity.User;
import com.example.MovieTicketBooking.service.BookingService;
import com.example.MovieTicketBooking.service.PaymentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BookingService bookingService;

    /**
     * Show payment page for a booking
     */
    @GetMapping("/{bookingId}")
    public String showPaymentPage(@PathVariable Long bookingId, Model model, Authentication authentication) {
        try {
            User user = (User) authentication.getPrincipal();
            Booking booking = bookingService.getBookingById(bookingId);
            
            if (booking == null) {
                logger.error("Booking not found for ID: {}", bookingId);
                return "redirect:/bookings";
            }
            
            if (!booking.getUser().getId().equals(user.getId())) {
                logger.error("Unauthorized access to booking {} by user {}", bookingId, user.getId());
                return "redirect:/bookings";
            }
            
            // Check if payment already exists
            Payment existingPayment = paymentService.getPaymentByBookingId(bookingId);
            if (existingPayment != null && existingPayment.isSuccessful()) {
                logger.info("Payment already completed for booking {}", bookingId);
                return "redirect:/booking/confirmation?bookingReference=" + booking.getBookingReference();
            }
            
            // Create payment session
            String paymentSessionId = paymentService.createPaymentSession(booking);
            logger.info("Payment session created: {}", paymentSessionId);
            
            model.addAttribute("booking", booking);
            model.addAttribute("paymentSessionId", paymentSessionId);
            model.addAttribute("cashfreeAppId", paymentService.getCashfreeAppId());
            model.addAttribute("environment", paymentService.getCashfreeEnvironment());
            model.addAttribute("user", user);
            
            logger.info("Payment page attributes set - Booking ID: {}, Session ID: {}", booking.getId(), paymentSessionId);
            
            return "payment";
        } catch (Exception e) {
            logger.error("Error loading payment page for booking {}: {}", bookingId, e.getMessage(), e);
            return "redirect:/bookings";
        }
    }

    /**
     * Verify payment after Cashfree callback
     */
    @PostMapping("/verify")
    public String verifyPayment(@RequestParam String orderId, 
                              @RequestParam String paymentId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            User user = (User) authentication.getPrincipal();
            
            // Verify payment with Cashfree
            boolean isPaymentValid = paymentService.verifyPayment(orderId, paymentId);
            
            if (isPaymentValid) {
                redirectAttributes.addFlashAttribute("success", "Payment successful! Your tickets have been booked.");
                return "redirect:/booking/confirmation?orderId=" + orderId;
            } else {
                redirectAttributes.addFlashAttribute("error", "Payment verification failed. Please try again.");
                return "redirect:/bookings";
            }
            
        } catch (Exception e) {
            logger.error("Payment verification failed: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Payment verification failed");
            return "redirect:/bookings";
        }
    }

    /**
     * Handle payment success callback from Cashfree
     */
    @GetMapping("/success")
    public String paymentSuccess(@RequestParam(required = false) String order_id,
                               @RequestParam(required = false) String cf_payment_id,
                               @RequestParam(required = false) String payment_status,
                               @RequestParam(required = false) String orderId,
                               @RequestParam(required = false) String paymentId,
                               @RequestParam(required = false) String status,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            // Handle different parameter names from Cashfree
            String actualOrderId = order_id != null ? order_id : orderId;
            String actualPaymentId = cf_payment_id != null ? cf_payment_id : paymentId;
            String actualStatus = payment_status != null ? payment_status : status;
            
            logger.info("Payment success callback - Order: {}, Payment: {}, Status: {}", actualOrderId, actualPaymentId, actualStatus);
            
            if (actualOrderId != null && actualPaymentId != null) {
                User user = (User) authentication.getPrincipal();
                
                // Verify payment with Cashfree API
                logger.info("Verifying payment with Cashfree API");
                boolean isPaymentValid = paymentService.verifyPayment(actualOrderId, actualPaymentId);
                
                if (isPaymentValid) {
                    logger.info("Payment verified successfully with Cashfree");
                    redirectAttributes.addFlashAttribute("success", "Payment successful! Your movie tickets have been booked.");
                    return "redirect:/booking/confirmation?orderId=" + actualOrderId;
                } else {
                    logger.warn("Payment verification failed for order: {}, payment: {}", actualOrderId, actualPaymentId);
                    redirectAttributes.addFlashAttribute("error", "Payment verification failed. Please contact support.");
                    return "redirect:/bookings";
                }
            }
            
            // If we reach here, payment might be successful but parameters are missing
            logger.warn("Payment callback with missing parameters - order_id: {}, cf_payment_id: {}", actualOrderId, actualPaymentId);
            
            // For college project, show success message and redirect to bookings
            redirectAttributes.addFlashAttribute("success", "Payment completed! Please check your bookings for confirmation.");
            return "redirect:/bookings";
            
        } catch (Exception e) {
            logger.error("Payment success processing failed: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Payment processing failed");
            return "redirect:/bookings";
        }
    }

    /**
     * Handle payment failure callback from Cashfree
     */
    @GetMapping("/failure")
    public String paymentFailure(@RequestParam(required = false) String order_id,
                               @RequestParam(required = false) String cf_payment_id,
                               @RequestParam(required = false) String payment_status,
                               @RequestParam(required = false) String orderId,
                               @RequestParam(required = false) String paymentId,
                               @RequestParam(required = false) String status,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        try {
            String actualOrderId = order_id != null ? order_id : orderId;
            String actualPaymentId = cf_payment_id != null ? cf_payment_id : paymentId;
            
            logger.info("Payment failure callback - Order: {}, Payment: {}", actualOrderId, actualPaymentId);
            
            redirectAttributes.addFlashAttribute("error", "Payment failed. Please try again or use a different payment method.");
            return "redirect:/bookings";
            
        } catch (Exception e) {
            logger.error("Payment failure processing failed: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Payment processing failed");
            return "redirect:/bookings";
        }
    }

    /**
     * Test payment success (for development/testing)
     */
    @PostMapping("/test/success/{bookingId}")
    public String testPaymentSuccess(@PathVariable Long bookingId, Authentication authentication, 
                                   RedirectAttributes redirectAttributes) {
        try {
            User user = (User) authentication.getPrincipal();
            Booking booking = bookingService.getBookingById(bookingId);
            
            if (booking == null || !booking.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access");
                return "redirect:/bookings";
            }
            
            // Simulate successful payment
            String testOrderId = "ORDER_" + bookingId + "_" + System.currentTimeMillis();
            String testPaymentId = "test_cashfree_payment_" + System.currentTimeMillis();
            
            boolean isPaymentValid = paymentService.verifyPayment(testOrderId, testPaymentId);
            
            if (isPaymentValid) {
                redirectAttributes.addFlashAttribute("success", "Test payment successful! Your movie tickets have been booked.");
                return "redirect:/booking/confirmation?orderId=" + testOrderId;
            } else {
                redirectAttributes.addFlashAttribute("error", "Test payment failed");
                return "redirect:/bookings";
            }
            
        } catch (Exception e) {
            logger.error("Test payment success failed: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Test payment failed");
            return "redirect:/bookings";
        }
    }

    /**
     * Test payment failure (for development/testing)
     */
    @PostMapping("/test/fail/{bookingId}")
    public String testPaymentFail(@PathVariable Long bookingId, Authentication authentication, 
                                RedirectAttributes redirectAttributes) {
        try {
            User user = (User) authentication.getPrincipal();
            Booking booking = bookingService.getBookingById(bookingId);
            
            if (booking == null || !booking.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access");
                return "redirect:/bookings";
            }
            
            // Simulate failed payment
            String testOrderId = "ORDER_" + bookingId + "_" + System.currentTimeMillis();
            String testPaymentId = "fail_test_payment_" + System.currentTimeMillis();
            
            boolean isPaymentValid = paymentService.verifyPayment(testOrderId, testPaymentId);
            
            if (!isPaymentValid) {
                redirectAttributes.addFlashAttribute("error", "Test payment failed as expected. Please try again.");
                return "redirect:/payment/" + bookingId;
            } else {
                redirectAttributes.addFlashAttribute("error", "Test payment unexpectedly succeeded");
                return "redirect:/bookings";
            }
            
        } catch (Exception e) {
            logger.error("Test payment fail simulation failed: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Test payment simulation failed");
            return "redirect:/bookings";
        }
    }

    /**
     * Handle Cashfree webhook
     */
    @PostMapping("/webhook")
    @ResponseBody
    public String handleWebhook(@RequestBody String payload, 
                              @RequestHeader(value = "x-webhook-signature", required = false) String signature) {
        try {
            logger.info("Received Cashfree webhook: {}", payload);
            
            // Verify webhook signature (for production)
            if (signature != null && !paymentService.verifyWebhookSignature(payload, signature)) {
                logger.error("Invalid webhook signature");
                return "ERROR";
            }
            
            // Parse webhook payload and update payment status
            // This is a simplified version for college project
            // In production, you would parse the JSON and update payment status accordingly
            
            return "OK";
        } catch (Exception e) {
            logger.error("Webhook processing failed: {}", e.getMessage());
            return "ERROR";
        }
    }
}