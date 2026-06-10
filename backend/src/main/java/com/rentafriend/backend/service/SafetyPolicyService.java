package com.rentafriend.backend.service;

import com.rentafriend.backend.dto.BookingPayload;
import com.rentafriend.backend.dto.LegalPolicyDto;
import com.rentafriend.backend.exception.ApiException;
import com.rentafriend.backend.model.SessionMode;
import com.rentafriend.backend.model.UserAccount;
import com.rentafriend.backend.model.VerificationStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

@Service
public class SafetyPolicyService {

    private static final String CURRENT_VERSION = "RAF-TC-2026-06";

    private static final List<String> PROHIBITED_TERMS = List.of(
            "sex",
            "sexual",
            "hookup",
            "escort",
            "intimacy",
            "intimate",
            "nude",
            "nudity",
            "kiss",
            "make out",
            "hotel room",
            "massage with extras"
    );

    public String getCurrentVersion() {
        return CURRENT_VERSION;
    }

    public LegalPolicyDto getCurrentPolicy() {
        return new LegalPolicyDto(
                CURRENT_VERSION,
                "Rent a Friend Terms, Conduct, and Safety Policy",
                "Rent a Friend is an emotional support and listening platform. It is not a dating, escort, sexual, or physical intimacy marketplace.",
                List.of(
                        "Users must be 18 or older and provide legal identity details during registration.",
                        "Users must provide a real phone number, emergency contact, and basic location details for account safety review.",
                        "Users must use the platform only for emotional support, companionship conversation, and non-clinical listening.",
                        "All users consent to identity review before in-person experiences are allowed."
                ),
                List.of(
                        "Requesting sex, sexual acts, nudity, escorting, pornography, fetish services, or physical intimacy.",
                        "Offering payment outside the platform for romantic, sexual, or hidden physical commitments.",
                        "Harassment, coercion, stalking, threats, hate speech, blackmail, or doxxing.",
                        "Asking listeners to meet in private unsafe environments or to ignore platform boundaries."
                ),
                List.of(
                        "In-person sessions require submitted identity details and a verified account status.",
                        "Meet only in safe public settings approved by the platform workflow.",
                        "No touching, kissing, sexual behavior, room-sharing, or overnight expectations.",
                        "Any boundary violation may lead to cancellation, account suspension, and record retention for safety review."
                ),
                "Accounts, bookings, and stored verification data may be reviewed and blocked when conduct suggests sexual services, abuse, or unsafe in-person intent."
        );
    }

    public void validateBookingRequest(UserAccount user, BookingPayload payload) {
        String content = (payload.category() + " " + (payload.notes() == null ? "" : payload.notes()))
                .toLowerCase(Locale.ROOT);

        for (String term : PROHIBITED_TERMS) {
            if (content.contains(term)) {
                throw new ApiException("This platform does not allow sexual, escort, or unsafe physical requests.");
            }
        }

        if (SessionMode.IN_PERSON.name().equalsIgnoreCase(payload.sessionMode())
                && user.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new ApiException("In-person sessions are available only after identity verification is approved.");
        }
    }
}

