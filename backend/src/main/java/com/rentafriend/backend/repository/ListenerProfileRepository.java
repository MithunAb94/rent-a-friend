package com.rentafriend.backend.repository;

import com.rentafriend.backend.model.ListenerProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListenerProfileRepository extends JpaRepository<ListenerProfile, Long> {
    List<ListenerProfile> findAllByOrderByFeaturedDescRatingDescDisplayNameAsc();
}

