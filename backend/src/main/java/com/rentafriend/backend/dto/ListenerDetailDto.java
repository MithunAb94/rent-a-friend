package com.rentafriend.backend.dto;

import java.util.List;

public record ListenerDetailDto(
        Long id,
        String displayName,
        String title,
        String bio,
        Integer yearsExperience,
        Double rating,
        Double hourlyRate,
        String city,
        List<String> languages,
        List<String> supportAreas,
        String availabilityNote,
        String responseTime,
        String imageUrl,
        boolean featured
) {
}

