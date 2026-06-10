package com.rentafriend.backend.controller;

import com.rentafriend.backend.dto.LegalPolicyDto;
import com.rentafriend.backend.dto.SupportCategoryDto;
import com.rentafriend.backend.repository.SupportCategoryRepository;
import com.rentafriend.backend.service.SafetyPolicyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/meta")
public class MetaController {

    private final SupportCategoryRepository supportCategoryRepository;
    private final SafetyPolicyService safetyPolicyService;

    public MetaController(SupportCategoryRepository supportCategoryRepository,
                          SafetyPolicyService safetyPolicyService) {
        this.supportCategoryRepository = supportCategoryRepository;
        this.safetyPolicyService = safetyPolicyService;
    }

    @GetMapping("/categories")
    public List<SupportCategoryDto> getCategories() {
        return supportCategoryRepository.findAll().stream()
                .map(category -> new SupportCategoryDto(
                        category.getId(),
                        category.getName(),
                        category.getIcon(),
                        category.getDescription()
                ))
                .toList();
    }

    @GetMapping("/legal-policy")
    public LegalPolicyDto getLegalPolicy() {
        return safetyPolicyService.getCurrentPolicy();
    }
}
