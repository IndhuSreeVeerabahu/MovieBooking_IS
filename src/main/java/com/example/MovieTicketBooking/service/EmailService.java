package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.entity.Booking;
import com.example.MovieTicketBooking.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.mail.from:noreply@moviehub.com}")
    private String fromEmail;

    @Value("${app.mail.from-name:MovieHub}")
    private String fromName;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    /**
     * Send booking confirmation email
     */
    public void sendBookingConfirmation(Booking booking) {
        try {
            log.info("Sending booking confirmation email for booking: {}", booking.getBookingReference());
            
            User user = booking.getUser();
            String subject = "Booking Confirmed - " + booking.getBookingReference();
            
            // Prepare template variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFirstName() + " " + user.getLastName());
            variables.put("bookingReference", booking.getBookingReference());
            variables.put("movieTitle", booking.getShow().getMovie().getTitle());
            variables.put("theaterName", booking.getShow().getTheater().getName());
            variables.put("screenName", booking.getShow().getScreen().getScreenDisplayName());
            variables.put("showDate", booking.getShow().getShowDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
            variables.put("showTime", booking.getShow().getShowTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            variables.put("totalAmount", booking.getTotalAmount());
            variables.put("bookingFee", booking.getBookingFee());
            variables.put("taxAmount", booking.getTaxAmount());
            variables.put("seats", booking.getBookingSeats());
            variables.put("baseUrl", baseUrl);
            
            sendHtmlEmail(user.getEmail(), subject, "booking-confirmation", variables);
            
        } catch (Exception e) {
            log.error("Failed to send booking confirmation email for booking: {}", 
                    booking.getBookingReference(), e);
        }
    }

    /**
     * Send booking cancellation email
     */
    public void sendBookingCancellation(Booking booking) {
        try {
            log.info("Sending booking cancellation email for booking: {}", booking.getBookingReference());
            
            User user = booking.getUser();
            String subject = "Booking Cancelled - " + booking.getBookingReference();
            
            // Prepare template variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFirstName() + " " + user.getLastName());
            variables.put("bookingReference", booking.getBookingReference());
            variables.put("movieTitle", booking.getShow().getMovie().getTitle());
            variables.put("theaterName", booking.getShow().getTheater().getName());
            variables.put("showDate", booking.getShow().getShowDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
            variables.put("showTime", booking.getShow().getShowTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            variables.put("cancellationReason", booking.getCancellationReason());
            variables.put("refundAmount", booking.getTotalAmount());
            variables.put("baseUrl", baseUrl);
            
            sendHtmlEmail(user.getEmail(), subject, "booking-cancellation", variables);
            
        } catch (Exception e) {
            log.error("Failed to send booking cancellation email for booking: {}", 
                    booking.getBookingReference(), e);
        }
    }

    /**
     * Send booking reminder email
     */
    public void sendBookingReminder(Booking booking) {
        try {
            log.info("Sending booking reminder email for booking: {}", booking.getBookingReference());
            
            User user = booking.getUser();
            String subject = "Movie Reminder - " + booking.getShow().getMovie().getTitle();
            
            // Prepare template variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFirstName() + " " + user.getLastName());
            variables.put("bookingReference", booking.getBookingReference());
            variables.put("movieTitle", booking.getShow().getMovie().getTitle());
            variables.put("theaterName", booking.getShow().getTheater().getName());
            variables.put("screenName", booking.getShow().getScreen().getScreenDisplayName());
            variables.put("showDate", booking.getShow().getShowDate().format(DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy")));
            variables.put("showTime", booking.getShow().getShowTime().format(DateTimeFormatter.ofPattern("hh:mm a")));
            variables.put("seats", booking.getBookingSeats());
            variables.put("baseUrl", baseUrl);
            
            sendHtmlEmail(user.getEmail(), subject, "booking-reminder", variables);
            
        } catch (Exception e) {
            log.error("Failed to send booking reminder email for booking: {}", 
                    booking.getBookingReference(), e);
        }
    }

    /**
     * Send welcome email to new user
     */
    public void sendWelcomeEmail(User user) {
        try {
            log.info("Sending welcome email to user: {}", user.getEmail());
            
            String subject = "Welcome to MovieHub!";
            
            // Prepare template variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFirstName() + " " + user.getLastName());
            variables.put("baseUrl", baseUrl);
            
            sendHtmlEmail(user.getEmail(), subject, "welcome", variables);
            
        } catch (Exception e) {
            log.error("Failed to send welcome email to user: {}", user.getEmail(), e);
        }
    }

    /**
     * Send password reset email
     */
    public void sendPasswordResetEmail(User user, String resetToken) {
        try {
            log.info("Sending password reset email to user: {}", user.getEmail());
            
            String subject = "Password Reset - MovieHub";
            
            // Prepare template variables
            Map<String, Object> variables = new HashMap<>();
            variables.put("userName", user.getFirstName() + " " + user.getLastName());
            variables.put("resetToken", resetToken);
            variables.put("resetUrl", baseUrl + "/reset-password?token=" + resetToken);
            variables.put("baseUrl", baseUrl);
            
            sendHtmlEmail(user.getEmail(), subject, "password-reset", variables);
            
        } catch (Exception e) {
            log.error("Failed to send password reset email to user: {}", user.getEmail(), e);
        }
    }

    /**
     * Send simple text email
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            
            mailSender.send(message);
            log.info("Simple email sent successfully to: {}", to);
            
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", to, e);
        }
    }

    /**
     * Send HTML email using Thymeleaf template
     */
    private void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
            
            try {
                helper.setFrom(fromEmail, fromName);
            } catch (UnsupportedEncodingException e) {
                log.warn("Failed to set from name, using email only: {}", e.getMessage());
                helper.setFrom(fromEmail);
            }
            helper.setTo(to);
            helper.setSubject(subject);
            
            // Process Thymeleaf template
            Context context = new Context();
            if (variables != null) {
                variables.forEach(context::setVariable);
            }
            
            String htmlContent = templateEngine.process("emails/" + templateName, context);
            helper.setText(htmlContent, true);
            
            mailSender.send(mimeMessage);
            log.info("HTML email sent successfully to: {}", to);
            
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
            throw new RuntimeException("Failed to send email", e);
        }
    }

    /**
     * Send test email
     */
    public void sendTestEmail(String to) {
        try {
            String subject = "Test Email from MovieHub";
            String text = "This is a test email from MovieHub. If you received this, the email service is working correctly.";
            
            sendSimpleEmail(to, subject, text);
            
        } catch (Exception e) {
            log.error("Failed to send test email to: {}", to, e);
        }
    }
}
