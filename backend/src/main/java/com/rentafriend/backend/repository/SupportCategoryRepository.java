package com.rentafriend.backend.repository;

import com.rentafriend.backend.model.SupportCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupportCategoryRepository extends JpaRepository<SupportCategory, Long> {
}

