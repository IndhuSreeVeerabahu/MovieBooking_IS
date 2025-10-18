package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.entity.Booking;
import com.example.MovieTicketBooking.entity.Payment;
import com.example.MovieTicketBooking.entity.PaymentStatus;
import com.example.MovieTicketBooking.entity.User;
import com.example.MovieTicketBooking.service.BookingService;
import com.example.MovieTicketBooking.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private BookingService bookingService;

    @Autowired
    private PaymentService paymentService;

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
            
            String paymentSessionId = paymentService.createPaymentSession(booking);
            logger.info("Payment session created: {}", paymentSessionId);
            
            model.addAttribute("booking", booking);
            model.addAttribute("paymentSessionId", paymentSessionId);
            model.addAttribute("cashfreeAppId", paymentService.getCashfreeAppId());
            model.addAttribute("environment", paymentService.getCashfreeEnvironment());
            
            logger.info("Payment page attributes set - Booking ID: {}, Session ID: {}", booking.getId(), paymentSessionId);
            
            return "payment";
        } catch (Exception e) {
            logger.error("Error loading payment page for booking {}: {}", bookingId, e.getMessage(), e);
            return "redirect:/bookings";
        }
    }

    @PostMapping("/verify")
    public String verifyPayment(@RequestParam String orderId, 
                              @RequestParam String paymentId,
                              Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        try {
            User user = (User) authentication.getPrincipal();
            Booking booking = bookingService.getBookingById(Long.parseLong(orderId.replace("ORDER_", "").split("_")[0]));
            
            if (!booking.getUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "Unauthorized access");
                return "redirect:/bookings";
            }
            
            // Verify payment with Cashfree
            boolean isPaymentValid = paymentService.verifyPayment(orderId, paymentId);
            
            if (isPaymentValid) {
                bookingService.confirmBooking(booking.getId(), paymentId, "UPI");
                redirectAttributes.addFlashAttribute("success", "Payment successful! Booking confirmed.");
                return "redirect:/booking/confirmation?bookingReference=" + booking.getBookingReference();
            } else {
                redirectAttributes.addFlashAttribute("error", "Payment verification failed. Please try again.");
                return "redirect:/bookings";
            }
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Payment verification failed");
            return "redirect:/bookings";
        }
    }

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
            
            logger.info("=== DUMMY PAYMENT SUCCESS CALLBACK ===");
            logger.info("Order: {}, Payment: {}, Status: {}", actualOrderId, actualPaymentId, actualStatus);
            
            // For dummy project - always succeed and confirm booking
            if (actualOrderId != null) {
                try {
                    // Extract booking ID from order ID
                    String bookingIdStr = actualOrderId.replace("ORDER_", "").split("_")[0];
                    Long bookingId = Long.parseLong(bookingIdStr);
                    
                    logger.info("Processing dummy payment success for booking ID: {}", bookingId);
                    
                    User user = (User) authentication.getPrincipal();
                    Booking booking = bookingService.getBookingById(bookingId);
                    
                    if (booking != null && booking.getUser().getId().equals(user.getId())) {
                        // Always confirm booking for dummy project
                        logger.info("DUMMY PROJECT: Confirming booking {}", bookingId);
                        bookingService.confirmBooking(bookingId, actualPaymentId != null ? actualPaymentId : "dummy_payment", "UPI");
                        
                        logger.info("✅ DUMMY PAYMENT SUCCESS: Booking {} confirmed!", bookingId);
                        redirectAttributes.addFlashAttribute("success", "Payment successful! Your movie tickets have been booked.");
                        return "redirect:/booking/confirmation?bookingReference=" + booking.getBookingReference();
                    } else {
                        logger.warn("Booking not found or unauthorized access");
                    }
                } catch (Exception e) {
                    logger.error("Error processing dummy payment success: {}", e.getMessage());
                }
            }
            
            // Fallback - always show success for dummy project
            logger.info("DUMMY PROJECT: Showing success message");
            redirectAttributes.addFlashAttribute("success", "Payment successful! Your movie tickets have been booked.");
            return "redirect:/bookings";
            
        } catch (Exception e) {
            logger.error("Payment success processing failed: {}", e.getMessage(), e);
            
            // Even if there's an error, show success for dummy project
            logger.info("DUMMY PROJECT: Showing success despite error");
            redirectAttributes.addFlashAttribute("success", "Payment successful! Your movie tickets have been booked.");
            return "redirect:/bookings";
        }
    }

    /**
     * Direct payment success for dummy project
     */
    @PostMapping("/direct-success/{bookingId}")
    public String directPaymentSuccess(@PathVariable Long bookingId,
                                     @RequestParam String paymentMethod,
                                     Authentication authentication,
                                     RedirectAttributes redirectAttributes) {
        try {
            logger.info("=== DIRECT DUMMY PAYMENT SUCCESS ===");
            logger.info("Booking ID: {}, Payment Method: {}", bookingId, paymentMethod);
            
            User user = (User) authentication.getPrincipal();
            Booking booking = bookingService.getBookingById(bookingId);
            
            if (booking != null && booking.getUser().getId().equals(user.getId())) {
                // Always confirm booking for dummy project
                logger.info("DUMMY PROJECT: Confirming booking {}", bookingId);
                bookingService.confirmBooking(bookingId, "direct_dummy_payment_" + System.currentTimeMillis(), paymentMethod);
                
                logger.info("✅ DIRECT DUMMY PAYMENT SUCCESS: Booking {} confirmed!", bookingId);
                redirectAttributes.addFlashAttribute("success", "Payment successful! Your movie tickets have been booked.");
                return "redirect:/booking/confirmation?bookingReference=" + booking.getBookingReference();
            } else {
                redirectAttributes.addFlashAttribute("error", "Booking not found or unauthorized access");
                return "redirect:/bookings";
            }
            
        } catch (Exception e) {
            logger.error("Direct payment success failed: {}", e.getMessage(), e);
            redirectAttributes.addFlashAttribute("error", "Payment processing failed");
            return "redirect:/bookings";
        }
    }

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
            
            // Parse webhook payload and update booking status
            // In production, you would parse the JSON and update booking status accordingly
            
            return "OK";
        } catch (Exception e) {
            logger.error("Webhook processing failed: {}", e.getMessage());
            return "ERROR";
        }
    }
}