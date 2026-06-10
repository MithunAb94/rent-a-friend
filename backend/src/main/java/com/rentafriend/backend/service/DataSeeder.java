package com.rentafriend.backend.service;

import com.rentafriend.backend.model.ListenerProfile;
import com.rentafriend.backend.model.SupportCategory;
import com.rentafriend.backend.repository.ListenerProfileRepository;
import com.rentafriend.backend.repository.SupportCategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final SupportCategoryRepository supportCategoryRepository;
    private final ListenerProfileRepository listenerProfileRepository;

    public DataSeeder(SupportCategoryRepository supportCategoryRepository,
                      ListenerProfileRepository listenerProfileRepository) {
        this.supportCategoryRepository = supportCategoryRepository;
        this.listenerProfileRepository = listenerProfileRepository;
    }

    @Override
    public void run(String... args) {
        seedCategories();
        seedListeners();
    }

    private void seedCategories() {
        if (supportCategoryRepository.count() > 0) {
            return;
        }

        supportCategoryRepository.saveAll(List.of(
                category("Stress Relief", "Calm", "For users who need a grounded conversation after a heavy day."),
                category("Heartbreak", "Heart", "Supportive listening for loss, breakups, and emotional recovery."),
                category("Life Direction", "Compass", "Talk through uncertainty, major decisions, and next steps."),
                category("Confidence Boost", "Spark", "Encouragement and perspective when self-doubt feels loud."),
                category("Just Need To Talk", "Chat", "A kind human presence when you simply need someone to listen.")
        ));
    }

    private void seedListeners() {
        if (listenerProfileRepository.count() > 0) {
            return;
        }

        listenerProfileRepository.saveAll(List.of(
                listener(
                        "Maya Johnson",
                        "Warm listener for stress, burnout, and emotional reset",
                        "Maya creates a calm space for people who feel overloaded. She blends attentive listening with gentle prompts that help users slow down, reflect, and leave a session feeling steadier.",
                        6,
                        4.9,
                        24.0,
                        "Bengaluru",
                        List.of("English", "Hindi"),
                        List.of("Stress Relief", "Confidence Boost", "Just Need To Talk"),
                        "Available evenings and weekends",
                        "Replies within 20 minutes",
                        "https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=600&q=80",
                        true
                ),
                listener(
                        "Daniel Carter",
                        "Grounded support for life transitions and loneliness",
                        "Daniel is known for helping users unpack tough moments without judgment. He is especially helpful for relocation stress, career uncertainty, and feeling disconnected.",
                        8,
                        4.8,
                        28.0,
                        "Hyderabad",
                        List.of("English"),
                        List.of("Life Direction", "Just Need To Talk", "Stress Relief"),
                        "Open for morning and lunchtime sessions",
                        "Replies within 35 minutes",
                        "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=600&q=80",
                        true
                ),
                listener(
                        "Asha Menon",
                        "Gentle encouragement for heartbreak and rebuilding confidence",
                        "Asha offers compassionate listening for people navigating breakup pain, grief, or self-worth struggles. Her sessions focus on emotional safety and practical reassurance.",
                        5,
                        4.95,
                        26.0,
                        "Chennai",
                        List.of("English", "Tamil"),
                        List.of("Heartbreak", "Confidence Boost", "Just Need To Talk"),
                        "Flexible afternoon slots",
                        "Replies within 15 minutes",
                        "https://images.unsplash.com/photo-1488426862026-3ee34a7d66df?auto=format&fit=crop&w=600&q=80",
                        true
                ),
                listener(
                        "Rohan Shah",
                        "Motivational listener for confidence and clarity",
                        "Rohan combines empathy with structured conversations. Users often book him when they need help organizing racing thoughts and getting back into motion.",
                        4,
                        4.7,
                        22.0,
                        "Mumbai",
                        List.of("English", "Hindi", "Gujarati"),
                        List.of("Confidence Boost", "Life Direction", "Stress Relief"),
                        "Late-night chat-friendly",
                        "Replies within 30 minutes",
                        "https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=600&q=80",
                        false
                )
        ));
    }

    private SupportCategory category(String name, String icon, String description) {
        SupportCategory category = new SupportCategory();
        category.setName(name);
        category.setIcon(icon);
        category.setDescription(description);
        return category;
    }

    private ListenerProfile listener(String displayName,
                                     String title,
                                     String bio,
                                     Integer yearsExperience,
                                     Double rating,
                                     Double hourlyRate,
                                     String city,
                                     List<String> languages,
                                     List<String> supportAreas,
                                     String availabilityNote,
                                     String responseTime,
                                     String imageUrl,
                                     boolean featured) {
        ListenerProfile listener = new ListenerProfile();
        listener.setDisplayName(displayName);
        listener.setTitle(title);
        listener.setBio(bio);
        listener.setYearsExperience(yearsExperience);
        listener.setRating(rating);
        listener.setHourlyRate(hourlyRate);
        listener.setCity(city);
        listener.setLanguages(languages);
        listener.setSupportAreas(supportAreas);
        listener.setAvailabilityNote(availabilityNote);
        listener.setResponseTime(responseTime);
        listener.setImageUrl(imageUrl);
        listener.setFeatured(featured);
        return listener;
    }
}

