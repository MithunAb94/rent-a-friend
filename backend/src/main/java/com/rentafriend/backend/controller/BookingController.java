package com.rentafriend.backend.controller;

import com.rentafriend.backend.dto.BookingPayload;
import com.rentafriend.backend.dto.BookingSummaryDto;
import com.rentafriend.backend.dto.StatusUpdateRequest;
import com.rentafriend.backend.model.UserAccount;
import com.rentafriend.backend.service.BookingService;
import com.rentafriend.backend.service.SessionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final SessionService sessionService;

    public BookingController(BookingService bookingService, SessionService sessionService) {
        this.bookingService = bookingService;
        this.sessionService = sessionService;
    }

    @PostMapping
    public BookingSummaryDto createBooking(@RequestHeader("Authorization") String authorization,
                                           @Valid @RequestBody BookingPayload payload) {
        UserAccount user = sessionService.requireUser(authorization);
        return bookingService.createBooking(user, payload);
    }

    @GetMapping("/me")
    public List<BookingSummaryDto> getMyBookings(@RequestHeader("Authorization") String authorization) {
        UserAccount user = sessionService.requireUser(authorization);
        return bookingService.getBookingsForUser(user);
    }

    @PatchMapping("/{bookingId}/status")
    public BookingSummaryDto updateStatus(@RequestHeader("Authorization") String authorization,
                                          @PathVariable Long bookingId,
                                          @Valid @RequestBody StatusUpdateRequest request) {
        UserAccount user = sessionService.requireUser(authorization);
        return bookingService.updateStatus(user, bookingId, request);
    }
}

