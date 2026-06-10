package com.rentafriend.backend.service;

import com.rentafriend.backend.dto.BookingSummaryDto;
import com.rentafriend.backend.dto.DashboardResponse;
import com.rentafriend.backend.dto.DashboardStatsDto;
import com.rentafriend.backend.model.UserAccount;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardService {

    private final AuthService authService;
    private final BookingService bookingService;
    private final ListenerService listenerService;

    public DashboardService(AuthService authService, BookingService bookingService, ListenerService listenerService) {
        this.authService = authService;
        this.bookingService = bookingService;
        this.listenerService = listenerService;
    }

    public DashboardResponse getDashboard(UserAccount user) {
        List<BookingSummaryDto> bookings = bookingService.getBookingsForUser(user);
        DashboardStatsDto stats = new DashboardStatsDto(
                bookings.size(),
                bookingService.countUpcoming(bookings),
                bookingService.countCompleted(bookings)
        );

        return new DashboardResponse(
                authService.toUserProfile(user),
                stats,
                bookings,
                listenerService.getRecommendations(3)
        );
    }
}

