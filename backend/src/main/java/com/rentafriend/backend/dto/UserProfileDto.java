package com.rentafriend.backend.dto;

import java.time.LocalDateTime;

public record UserProfileDto(
        Long id,
        String fullName,
        String legalName,
        String email,
        String phoneNumber,
        String city,
        String state,
        String country,
        Double latitude,
        Double longitude,
        boolean locationConsentGranted,
        String emotionalGoal,
        String preferredSupportStyle,
        String verificationStatus,
        String governmentIdType,
        String maskedGovernmentId,
        String emergencyContactName,
        String emergencyContactPhone,
        boolean emailVerified,
        boolean phoneVerified,
        String termsVersion,
        LocalDateTime termsAcceptedAt,
        LocalDateTime safetyPolicyAcceptedAt,
        LocalDateTime physicalBoundaryAcceptedAt,
        LocalDateTime verificationSubmittedAt
) {
}

