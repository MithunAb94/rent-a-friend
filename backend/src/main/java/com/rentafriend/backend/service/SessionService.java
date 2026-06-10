package com.rentafriend.backend.service;

import com.rentafriend.backend.exception.ApiException;
import com.rentafriend.backend.model.UserAccount;
import com.rentafriend.backend.model.UserSession;
import com.rentafriend.backend.repository.UserSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SessionService {

    private final UserSessionRepository userSessionRepository;

    public SessionService(UserSessionRepository userSessionRepository) {
        this.userSessionRepository = userSessionRepository;
    }

    public UserAccount requireUser(String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new ApiException("Please sign in to continue.");
        }

        String token = authorizationHeader.substring(7).trim();
        UserSession session = userSessionRepository.findById(token)
                .orElseThrow(() -> new ApiException("Your session has expired. Please sign in again."));

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            userSessionRepository.delete(session);
            throw new ApiException("Your session has expired. Please sign in again.");
        }

        return session.getUser();
    }
}

