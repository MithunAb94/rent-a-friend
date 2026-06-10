package com.rentafriend.backend.repository;

import com.rentafriend.backend.model.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSessionRepository extends JpaRepository<UserSession, String> {
}

