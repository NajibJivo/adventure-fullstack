package com.example.miniProjekt.service;


import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.repository.ActivityRepository;
import com.example.miniProjekt.service.exceptions.ActivityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;

    // Constructor injection
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

        /**CREATE **/
    @Transactional
    public Activity create(Activity input) {
        validate(input);
        input.setId(null); // sikkerhed: JPA skal selv generere id
        return activityRepository.save(input);
    }

    private void validate(Activity a) {
        if (a.getName() == null || a.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (a.getPrice() == null || a.getPrice().signum() < 0) {
            throw new IllegalArgumentException("Price must be >= 0");
        }
        if (a.getDuration() == null || a.getDuration() <= 0) {
            throw new IllegalArgumentException("Duration must be > 0");
        }
        if (a.getMinAge() == null || a.getMinAge() < 0) {
            throw new IllegalArgumentException("minAge must be >= 0");
        }
        if (a.getMinHeight() == null || a.getMinHeight() < 0) {
            throw new IllegalArgumentException("minHeight must be >= 0");
        }
        if (a.getAvailableFrom() != null && a.getAvailableTo() != null
                && a.getAvailableFrom().isAfter(a.getAvailableTo())) {
            throw new IllegalArgumentException("availableFrom must be before availableTo");
        }
        // imageUrl er valgfri – tilføj evt. format-check senere
    }

        /** READ - All**/
    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

        /** READ - Single**/
    public Activity getByIdOrThrow(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
    }

        /** UPDATE **/
    @Transactional
    public Activity update(Long id, Activity input) {
        Activity existing = getByIdOrThrow(id); // 404 hvis ikke findes

        // Kopiér felter (fuld opdatering)
        existing.setName(input.getName());
        existing.setDescription(input.getDescription());
        existing.setPrice(input.getPrice());
        existing.setDuration(input.getDuration());
        existing.setMinAge(input.getMinAge());
        existing.setMinHeight(input.getMinHeight());
        existing.setAvailableFrom(input.getAvailableFrom());
        existing.setAvailableTo(input.getAvailableTo());
        existing.setImageUrl(input.getImageUrl());

        validate(existing);                 // samme validering som create()
        return activityRepository.save(existing);
    }

        /** DELETE **/
    @Transactional
    public void delete(Long id) {
        Activity existing = getByIdOrThrow(id); // kaster 404-exception hvis mangler
        activityRepository.delete(existing);
    }
}
