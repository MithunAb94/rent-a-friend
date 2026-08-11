package com.rentafriend.backend.service;

import com.rentafriend.backend.dto.BookingPayload;
import com.rentafriend.backend.dto.BookingSummaryDto;
import com.rentafriend.backend.dto.StatusUpdateRequest;
import com.rentafriend.backend.exception.ApiException;
import com.rentafriend.backend.model.*;
import com.rentafriend.backend.repository.BookingRequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class BookingService {

    private final BookingRequestRepository bookingRequestRepository;
    private final ListenerService listenerService;
    private final SafetyPolicyService safetyPolicyService;

    public BookingService(BookingRequestRepository bookingRequestRepository,
                          ListenerService listenerService,
                          SafetyPolicyService safetyPolicyService) {
        this.bookingRequestRepository = bookingRequestRepository;
        this.listenerService = listenerService;
        this.safetyPolicyService = safetyPolicyService;
    }

    @Transactional
    public BookingSummaryDto createBooking(UserAccount user, BookingPayload payload) {
        ListenerProfile listener = listenerService.requireListener(payload.listenerId());
        safetyPolicyService.validateBookingRequest(user, payload);

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setUser(user);
        bookingRequest.setListener(listener);
        bookingRequest.setCategory(payload.category().trim());
        bookingRequest.setSessionMode(parseSessionMode(payload.sessionMode()));
        bookingRequest.setPreferredDate(payload.preferredDate());
        bookingRequest.setPreferredTime(payload.preferredTime().trim());
        bookingRequest.setDurationMinutes(payload.durationMinutes());
        bookingRequest.setNotes(payload.notes() == null ? "" : payload.notes().trim());
        bookingRequest.setStatus(BookingStatus.PENDING);
        bookingRequest.setCreatedAt(LocalDateTime.now());

        BookingRequest savedBooking = bookingRequestRepository.save(bookingRequest);
        return toSummaryDto(savedBooking);
    }

    @Transactional(readOnly = true)
    public List<BookingSummaryDto> getBookingsForUser(UserAccount user) {
        return bookingRequestRepository.findByUserIdOrderByPreferredDateAscCreatedAtDesc(user.getId())
                .stream()
                .map(this::toSummaryDto)
                .toList();
    }

    @Transactional
    public BookingSummaryDto updateStatus(UserAccount user, Long bookingId, StatusUpdateRequest request) {
        BookingRequest booking = bookingRequestRepository.findById(bookingId)
                .orElseThrow(() -> new ApiException("Booking not found."));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new ApiException("You can only update your own bookings.");
        }

        booking.setStatus(parseStatus(request.status()));
        return toSummaryDto(bookingRequestRepository.save(booking));
    }

    public long countCompleted(List<BookingSummaryDto> bookings) {
        return bookings.stream().filter(booking -> "COMPLETED".equals(booking.status())).count();
    }

    public long countUpcoming(List<BookingSummaryDto> bookings) {
        LocalDate today = LocalDate.now();
        return bookings.stream()
                .filter(booking -> !booking.preferredDate().isBefore(today))
                .filter(booking -> !"CANCELLED".equals(booking.status()))
                .count();
    }

    private SessionMode parseSessionMode(String sessionMode) {
        try {
            return SessionMode.valueOf(sessionMode.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ApiException("Unsupported session mode.");
        }
    }

    private BookingStatus parseStatus(String status) {
        try {
            return BookingStatus.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            throw new ApiException("Unsupported booking status.");
        }
    }

    public BookingSummaryDto toSummaryDto(BookingRequest booking) {
        return new BookingSummaryDto(
                booking.getId(),
                booking.getListener().getDisplayName(),
                booking.getListener().getTitle(),
                booking.getCategory(),
                booking.getSessionMode().name(),
                booking.getPreferredDate(),
                booking.getPreferredTime(),
                booking.getDurationMinutes(),
                booking.getNotes(),
                booking.getStatus().name(),
                booking.getCreatedAt()
        );
    }
}

