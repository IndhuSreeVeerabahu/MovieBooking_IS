package com.example.MovieTicketBooking.controller;

import com.example.MovieTicketBooking.dto.*;
import com.example.MovieTicketBooking.entity.User;
import com.example.MovieTicketBooking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UserController {
    
    private final UserService userService;
    
    /**
     * Get current user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getCurrentUserProfile(Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            UserResponse response = userService.getUserProfile(currentUser.getId());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to get user profile", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve user profile"));
        }
    }
    
    /**
     * Update current user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateCurrentUserProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            UserResponse response = userService.updateProfile(currentUser.getId(), request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to update user profile", e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * Change user password
     */
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        try {
            User currentUser = (User) authentication.getPrincipal();
            userService.changePassword(currentUser.getId(), request);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to change password", e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return errorResponse;
    }
}

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
class AdminController {
    
    private final UserService userService;
    
    /**
     * Get all users (Admin only)
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        try {
            log.info("Admin requesting all users");
            List<UserResponse> users = userService.getAllUsers();
            log.info("Returning {} users to admin", users.size());
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            log.error("Failed to get all users", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve users: " + e.getMessage()));
        }
    }
    
    /**
     * Debug endpoint to test users API (temporary)
     */
    @GetMapping("/users/debug")
    public ResponseEntity<?> debugUsers() {
        try {
            log.info("Debug endpoint called");
            List<UserResponse> users = userService.getAllUsers();
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "userCount", users.size(),
                "users", users
            ));
        } catch (Exception e) {
            log.error("Debug endpoint error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                        "status", "error",
                        "message", e.getMessage()
                    ));
        }
    }
    
    /**
     * Toggle user status (Admin only)
     */
    @PutMapping("/users/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> toggleUserStatus(@PathVariable Long userId) {
        try {
            UserResponse response = userService.toggleUserStatus(userId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to toggle user status for ID: {}", userId, e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Update user (Admin only)
     */
    @PutMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest request) {
        try {
            UserResponse response = userService.updateUser(userId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to update user with ID: {}", userId, e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * Delete user (Admin only)
     */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long userId) {
        try {
            userService.deleteUser(userId);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Failed to delete user with ID: {}", userId, e);
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return errorResponse;
    }
}
