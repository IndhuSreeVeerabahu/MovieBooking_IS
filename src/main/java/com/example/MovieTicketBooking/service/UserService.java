package com.example.MovieTicketBooking.service;

import com.example.MovieTicketBooking.dto.*;
import com.example.MovieTicketBooking.entity.User;
import com.example.MovieTicketBooking.repository.UserRepository;
import com.example.MovieTicketBooking.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    
    /**
     * Register a new user
     */
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("User already exists with email: " + request.getEmail());
        }
        
        // Create new user
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .role(User.Role.USER)
                .isEnabled(true)
                .build();
        
        user = userRepository.save(user);
        log.info("User registered successfully with ID: {}", user.getId());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        
        return AuthResponse.fromUser(user, token);
    }
    
    /**
     * Authenticate user and return JWT token
     */
    public AuthResponse authenticate(AuthRequest request) {
        log.info("Authenticating user with email: {}", request.getEmail());
        
        // Find user by email
        User user = userRepository.findByEmailAndIsEnabled(request.getEmail(), true)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }
        
        log.info("User authenticated successfully with ID: {}", user.getId());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(user);
        
        return AuthResponse.fromUser(user, token);
    }
    
    /**
     * Get user profile by ID
     */
    @Transactional(readOnly = true)
    public UserResponse getUserProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        return UserResponse.fromUser(user);
    }
    
    /**
     * Update user profile
     */
    public UserResponse updateUserProfile(Long userId, RegisterRequest request) {
        log.info("Updating user profile for ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Update user fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        
        // Update password if provided
        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        
        user = userRepository.save(user);
        log.info("User profile updated successfully for ID: {}", userId);
        
        return UserResponse.fromUser(user);
    }
    
    /**
     * Get all users (Admin only)
     */
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        log.info("Fetching all users for admin");
        try {
            List<User> users = userRepository.findAll();
            log.info("Found {} users in database", users.size());
            
            if (users.isEmpty()) {
                log.warn("No users found in database");
            }
            
            return users.stream()
                    .map(UserResponse::fromUser)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching users from database", e);
            throw new RuntimeException("Failed to fetch users: " + e.getMessage(), e);
        }
    }
    
    /**
     * Enable/Disable user (Admin only)
     */
    public UserResponse toggleUserStatus(Long userId) {
        log.info("Toggling user status for ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        user.setIsEnabled(!user.getIsEnabled());
        user = userRepository.save(user);
        
        log.info("User status toggled successfully for ID: {}", userId);
        return UserResponse.fromUser(user);
    }
    
    /**
     * Update user (Admin only)
     */
    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        log.info("Updating user with ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Check if email is being changed and if it already exists
        if (!user.getEmail().equals(request.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists: " + request.getEmail());
            }
        }
        
        // Update user fields
        user.setFirstName(request.getFullName().split(" ")[0]);
        user.setLastName(request.getFullName().contains(" ") ? 
            request.getFullName().substring(request.getFullName().indexOf(" ") + 1) : "");
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(User.Role.valueOf(request.getRole()));
        user.setIsEnabled(request.getIsActive());
        
        user = userRepository.save(user);
        log.info("User updated successfully with ID: {}", userId);
        
        return UserResponse.fromUser(user);
    }
    
    /**
     * Delete user (Admin only)
     */
    public void deleteUser(Long userId) {
        log.info("Deleting user with ID: {}", userId);
        
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found with ID: " + userId);
        }
        
        userRepository.deleteById(userId);
        log.info("User deleted successfully with ID: {}", userId);
    }

    /**
     * Update user profile information
     */
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        log.info("Updating profile for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Check if email is being changed
        if (!user.getEmail().equals(request.getEmail())) {
            // Verify current password for email change
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                throw new RuntimeException("Current password is required to change email");
            }
            
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                throw new RuntimeException("Current password is incorrect");
            }
            
            // Check if new email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new RuntimeException("Email already exists: " + request.getEmail());
            }
        }
        
        // Update user fields
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        
        user = userRepository.save(user);
        log.info("Profile updated successfully for user ID: {}", userId);
        
        return UserResponse.fromUser(user);
    }

    /**
     * Change user password
     */
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.info("Changing password for user ID: {}", userId);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        
        // Verify current password
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        // Verify new password confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New password and confirm password do not match");
        }
        
        // Update password
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        
        log.info("Password changed successfully for user ID: {}", userId);
    }

    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmailAndIsEnabled(email, true)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        return UserResponse.fromUser(user);
    }
}
