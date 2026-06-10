package com.rentafriend.backend.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record BookingSummaryDto(
        Long id,
        String listenerName,
        String listenerTitle,
        String category,
        String sessionMode,
        LocalDate preferredDate,
        String preferredTime,
        Integer durationMinutes,
        String notes,
        String status,
        LocalDateTime createdAt
) {
}

