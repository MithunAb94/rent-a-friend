package com.rentafriend.backend.repository;

import com.rentafriend.backend.model.BookingRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingRequestRepository extends JpaRepository<BookingRequest, Long> {
    List<BookingRequest> findByUserIdOrderByPreferredDateAscCreatedAtDesc(Long userId);
}

