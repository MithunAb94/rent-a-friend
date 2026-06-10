package com.rentafriend.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank String fullName,
        @NotBlank String legalName,
        @Email @NotBlank String email,
        @Size(min = 6, message = "Password must be at least 6 characters") String password,
        @NotBlank @Pattern(regexp = "^[+0-9()\\-\\s]{8,20}$", message = "Please enter a valid phone number") String phoneNumber,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank String country,
        Double latitude,
        Double longitude,
        @NotNull Boolean locationConsentGranted,
        @NotBlank String emotionalGoal,
        @NotBlank String preferredSupportStyle,
        @NotNull LocalDate dateOfBirth,
        @NotBlank String governmentIdType,
        @NotBlank @Pattern(regexp = "^\\d{4}$", message = "Enter the last 4 digits of the ID") String governmentIdLastFour,
        @NotBlank String emergencyContactName,
        @NotBlank @Pattern(regexp = "^[+0-9()\\-\\s]{8,20}$", message = "Please enter a valid emergency contact number") String emergencyContactPhone,
        @NotNull Boolean acceptedTerms,
        @NotNull Boolean acceptedSafetyPolicy,
        @NotNull Boolean acceptedPhysicalBoundaries
) {
}

