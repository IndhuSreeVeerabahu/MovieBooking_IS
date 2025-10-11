package com.example.MovieTicketBooking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TheaterResponse {

    private Long id;
    private String name;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String phoneNumber;
    private String email;
    private Integer totalScreens;
    private String amenities;
    private Boolean isActive;
    private String fullAddress;
    private List<ShowResponse> shows;
    private List<ScreenResponse> screens;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
