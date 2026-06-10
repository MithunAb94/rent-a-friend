package com.rentafriend.backend.dto;

import java.util.List;

public record ListenerCardDto(
        Long id,
        String displayName,
        String title,
        String city,
        Double rating,
        Double hourlyRate,
        String responseTime,
        String availabilityNote,
        String imageUrl,
        boolean featured,
        List<String> supportAreas
) {
}

