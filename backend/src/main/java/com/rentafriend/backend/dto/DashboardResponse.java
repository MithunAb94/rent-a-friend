package com.rentafriend.backend.dto;

import java.util.List;

public record DashboardResponse(
        UserProfileDto user,
        DashboardStatsDto stats,
        List<BookingSummaryDto> bookings,
        List<ListenerCardDto> recommendations
) {
}

