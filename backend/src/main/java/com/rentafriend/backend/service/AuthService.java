package com.rentafriend.backend.service;

import com.rentafriend.backend.dto.AuthRequest;
import com.rentafriend.backend.dto.AuthResponse;
import com.rentafriend.backend.dto.RegisterRequest;
import com.rentafriend.backend.dto.UserProfileDto;
import com.rentafriend.backend.exception.ApiException;
import com.rentafriend.backend.model.UserAccount;
import com.rentafriend.backend.model.UserSession;
import com.rentafriend.backend.model.VerificationStatus;
import com.rentafriend.backend.repository.UserAccountRepository;
import com.rentafriend.backend.repository.UserSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final UserSessionRepository userSessionRepository;
    private final SafetyPolicyService safetyPolicyService;

    public AuthService(UserAccountRepository userAccountRepository,
                       UserSessionRepository userSessionRepository,
                       SafetyPolicyService safetyPolicyService) {
        this.userAccountRepository = userAccountRepository;
        this.userSessionRepository = userSessionRepository;
        this.safetyPolicyService = safetyPolicyService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        userAccountRepository.findByEmailIgnoreCase(request.email())
                .ifPresent(user -> {
                    throw new ApiException("An account with this email already exists.");
                });

        validateRegistrationRequest(request);

        LocalDateTime now = LocalDateTime.now();

        UserAccount user = new UserAccount();
        user.setFullName(request.fullName().trim());
        user.setLegalName(request.legalName().trim());
        user.setEmail(request.email().trim().toLowerCase());
        user.setPasswordHash(hashPassword(request.password()));
        user.setPhoneNumber(request.phoneNumber().trim());
        user.setCity(request.city().trim());
        user.setState(request.state().trim());
        user.setCountry(request.country().trim());
        user.setLatitude(request.locationConsentGranted() ? request.latitude() : null);
        user.setLongitude(request.locationConsentGranted() ? request.longitude() : null);
        user.setLocationConsentGranted(Boolean.TRUE.equals(request.locationConsentGranted()));
        user.setLocationCapturedAt(request.latitude() != null && request.longitude() != null ? now : null);
        user.setEmotionalGoal(request.emotionalGoal().trim());
        user.setPreferredSupportStyle(request.preferredSupportStyle().trim());
        user.setDateOfBirth(request.dateOfBirth());
        user.setGovernmentIdType(request.governmentIdType().trim());
        user.setGovernmentIdLastFour(request.governmentIdLastFour().trim());
        user.setEmergencyContactName(request.emergencyContactName().trim());
        user.setEmergencyContactPhone(request.emergencyContactPhone().trim());
        user.setVerificationStatus(VerificationStatus.SUBMITTED);
        user.setEmailVerified(false);
        user.setPhoneVerified(false);
        user.setTermsVersion(safetyPolicyService.getCurrentVersion());
        user.setTermsAcceptedAt(now);
        user.setSafetyPolicyAcceptedAt(now);
        user.setPhysicalBoundaryAcceptedAt(now);
        user.setVerificationSubmittedAt(now);
        user.setVerificationNotes("Verification submitted and waiting for review. In-person sessions stay locked until approval.");
        user.setCreatedAt(now);

        UserAccount savedUser = userAccountRepository.save(user);
        return createAuthResponse(savedUser);
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        UserAccount user = userAccountRepository.findByEmailIgnoreCase(request.email())
                .orElseThrow(() -> new ApiException("No account found with this email."));

        if (!user.getPasswordHash().equals(hashPassword(request.password()))) {
            throw new ApiException("Incorrect password. Please try again.");
        }

        return createAuthResponse(user);
    }

    public UserProfileDto toUserProfile(UserAccount user) {
        return new UserProfileDto(
                user.getId(),
                user.getFullName(),
                user.getLegalName(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getCity(),
                user.getState(),
                user.getCountry(),
                user.getLatitude(),
                user.getLongitude(),
                user.isLocationConsentGranted(),
                user.getEmotionalGoal(),
                user.getPreferredSupportStyle(),
                user.getVerificationStatus() == null ? VerificationStatus.SUBMITTED.name() : user.getVerificationStatus().name(),
                user.getGovernmentIdType(),
                maskGovernmentId(user.getGovernmentIdLastFour()),
                user.getEmergencyContactName(),
                maskPhoneNumber(user.getEmergencyContactPhone()),
                user.isEmailVerified(),
                user.isPhoneVerified(),
                user.getTermsVersion(),
                user.getTermsAcceptedAt(),
                user.getSafetyPolicyAcceptedAt(),
                user.getPhysicalBoundaryAcceptedAt(),
                user.getVerificationSubmittedAt()
        );
    }

    private void validateRegistrationRequest(RegisterRequest request) {
        if (Boolean.FALSE.equals(request.acceptedTerms())) {
            throw new ApiException("You must accept the terms and conditions to register.");
        }

        if (Boolean.FALSE.equals(request.acceptedSafetyPolicy())) {
            throw new ApiException("You must accept the safety policy to register.");
        }

        if (Boolean.FALSE.equals(request.acceptedPhysicalBoundaries())) {
            throw new ApiException("You must accept the physical boundary rules to register.");
        }

        if (request.dateOfBirth() == null || Period.between(request.dateOfBirth(), LocalDate.now()).getYears() < 18) {
            throw new ApiException("Users must be at least 18 years old.");
        }

        if (Boolean.TRUE.equals(request.locationConsentGranted())
                && (request.latitude() == null || request.longitude() == null)) {
            throw new ApiException("Location consent was granted, but the current location was not captured.");
        }
    }

    private AuthResponse createAuthResponse(UserAccount user) {
        UserSession session = new UserSession();
        session.setToken(UUID.randomUUID().toString().replace("-", ""));
        session.setUser(user);
        session.setExpiresAt(LocalDateTime.now().plusDays(14));
        userSessionRepository.save(session);
        return new AuthResponse(session.getToken(), toUserProfile(user));
    }

    private String hashPassword(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Unable to hash password", exception);
        }
    }

    private String maskGovernmentId(String lastFour) {
        if (lastFour == null || lastFour.isBlank()) {
            return "Not submitted";
        }
        return "****" + lastFour;
    }

    private String maskPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.length() < 4) {
            return phoneNumber;
        }
        return "xxxxxx" + phoneNumber.substring(phoneNumber.length() - 4);
    }
}
