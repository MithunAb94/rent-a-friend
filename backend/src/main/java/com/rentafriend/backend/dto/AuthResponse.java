package com.rentafriend.backend.dto;

public record AuthResponse(
        String token,
        UserProfileDto user
) {
}

