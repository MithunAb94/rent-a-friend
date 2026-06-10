package com.rentafriend.backend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record BookingPayload(
        @NotNull Long listenerId,
        @NotBlank String category,
        @NotBlank String sessionMode,
        @NotNull @FutureOrPresent LocalDate preferredDate,
        @NotBlank String preferredTime,
        @NotNull @Min(30) @Max(180) Integer durationMinutes,
        String notes
) {
}

