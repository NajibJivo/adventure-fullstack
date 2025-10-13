package com.example.miniProjekt.service;


import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.repository.ActivityRepository;
import com.example.miniProjekt.service.exceptions.ActivityNotFoundException;
import com.example.miniProjekt.web.dto.ActivityRequest;
import com.example.miniProjekt.web.dto.ActivityResponse;
import com.example.miniProjekt.web.mapper.ActivityDtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class ActivityServiceDto {
    private final ActivityRepository activityRepository;

    // Constructor injection
    public ActivityServiceDto(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

        /**CREATE **/
    @Transactional
    public ActivityResponse create(ActivityRequest req) {
        Activity entity = apply(new Activity(), req);
        validate(entity);
        entity.setId(null);
        return toResponse(activityRepository.save(entity));
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

        /** READ (all) -> DTO**/
    public Page<ActivityResponse> list(Pageable pageable) {
        return activityRepository
                .findAll(pageable) // Page#map mapper hvert element
                .map(this::toResponse);
    }

        /** READ - Single**/
    public Activity getByIdOrThrow(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
    }

        /** UPDATE **/
    @Transactional
    public ActivityResponse update(Long id, ActivityRequest req) {
        Activity existing = activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
        apply(existing,req);
        validate(existing);
        return toResponse(activityRepository.save(existing));
    }

        /** DELETE **/
    @Transactional
    public void delete(Long id) {
        Activity existing = getByIdOrThrow(id); // kaster 404-exception hvis mangler
        activityRepository.delete(existing);
    }



    /** ================= mapning ============== **/
    private Activity apply(Activity target, ActivityRequest r) {
        ActivityDtoMapper.copy(r, target);
        return target;
    }

    private ActivityResponse toResponse(Activity a) {
        return new ActivityResponse(
                a.getId(), a.getName(), a.getDescription(), a.getPrice(),
                a.getDuration(), a.getMinAge(), a.getMinHeight(),
                a.getAvailableFrom(), a.getAvailableTo(), a.getImageUrl()
        );
    }

    /** Search - Spring Boot Web + Spring Data JPA klarer auto-bindingen af Pageable.
     * SØGNING + PAGINERING:
     * - query: valgfrit fritekst-filter (fx del af navn/beskrivelse).
     * - pageable: siger HVILKEN side, HVOR mange pr. side, og HVORDAN der sorteres.
     * - returnerer Page<T>: både selve resultaterne OG metadata (totalElements, totalPages, currentPage).
     *  Hvorfor?
     *  - Skalerbarhed: vi henter ikke “alt” på én gang (hurtigere og billigere).
     *  - UX: frontend kan vise “side 1/10”, “næste/forrige”, osv. **/
    public Page<ActivityResponse> search(String q, Pageable pageable) {
        return activityRepository.findByNameContainingIgnoreCase(q, pageable)
                .map(this::toResponse);
    }
}
