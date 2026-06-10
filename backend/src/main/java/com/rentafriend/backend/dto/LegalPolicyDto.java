package com.rentafriend.backend.dto;

import java.util.List;

public record LegalPolicyDto(
        String version,
        String title,
        String summary,
        List<String> userRequirements,
        List<String> prohibitedRequests,
        List<String> inPersonSafetyRules,
        String enforcementNote
) {
}

