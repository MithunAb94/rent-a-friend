package com.rentafriend.backend.service;

import com.rentafriend.backend.dto.ListenerCardDto;
import com.rentafriend.backend.dto.ListenerDetailDto;
import com.rentafriend.backend.exception.ApiException;
import com.rentafriend.backend.model.ListenerProfile;
import com.rentafriend.backend.repository.ListenerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListenerService {

    private final ListenerProfileRepository listenerProfileRepository;

    public ListenerService(ListenerProfileRepository listenerProfileRepository) {
        this.listenerProfileRepository = listenerProfileRepository;
    }

    @Transactional(readOnly = true)
    public List<ListenerCardDto> getAllListeners() {
        return listenerProfileRepository.findAllByOrderByFeaturedDescRatingDescDisplayNameAsc()
                .stream()
                .map(this::toCardDto)
                .toList();
    }

    public ListenerDetailDto getListener(Long id) {
        ListenerProfile listener = listenerProfileRepository.findById(id)
                .orElseThrow(() -> new ApiException("Listener profile not found."));
        return toDetailDto(listener);
    }

    @Transactional(readOnly = true)
    public List<ListenerCardDto> getRecommendations(int limit) {
        return listenerProfileRepository.findAllByOrderByFeaturedDescRatingDescDisplayNameAsc()
                .stream()
                .limit(limit)
                .map(this::toCardDto)
                .toList();
    }

    public ListenerProfile requireListener(Long id) {
        return listenerProfileRepository.findById(id)
                .orElseThrow(() -> new ApiException("Listener profile not found."));
    }

    public ListenerCardDto toCardDto(ListenerProfile listener) {
        return new ListenerCardDto(
                listener.getId(),
                listener.getDisplayName(),
                listener.getTitle(),
                listener.getCity(),
                listener.getRating(),
                listener.getHourlyRate(),
                listener.getResponseTime(),
                listener.getAvailabilityNote(),
                listener.getImageUrl(),
                listener.isFeatured(),
                listener.getSupportAreas()
        );
    }

    private ListenerDetailDto toDetailDto(ListenerProfile listener) {
        return new ListenerDetailDto(
                listener.getId(),
                listener.getDisplayName(),
                listener.getTitle(),
                listener.getBio(),
                listener.getYearsExperience(),
                listener.getRating(),
                listener.getHourlyRate(),
                listener.getCity(),
                listener.getLanguages(),
                listener.getSupportAreas(),
                listener.getAvailabilityNote(),
                listener.getResponseTime(),
                listener.getImageUrl(),
                listener.isFeatured()
        );
    }
}

