package com.rentafriend.backend.dto;

public record DashboardStatsDto(
        long totalSessions,
        long upcomingSessions,
        long completedSessions
) {
}

