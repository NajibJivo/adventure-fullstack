package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.model.ActivityEquipment;
import com.example.miniProjekt.model.Equipment;
import com.example.miniProjekt.repository.ActivityEquipmentRepository;
import com.example.miniProjekt.repository.ActivityRepository;
import com.example.miniProjekt.repository.EquipmentRepository;
import com.example.miniProjekt.web.dto.ActivityEquipmentRequest;
import com.example.miniProjekt.web.dto.ActivityEquipmentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service-lag for relationen mellem Activity og Equipment.
 * <p>
 * Håndterer al forretningslogik/validering for CRUD, og returnerer/forbruger
 * DTO'er så web-laget ikke er afhængigt af JPA-entities.
 */
@Service
public class ActivityEquipmentService {
    private final ActivityEquipmentRepository repo;
    private final ActivityRepository activityRepo;
    private final EquipmentRepository equipmentRepo;

    public ActivityEquipmentService(ActivityEquipmentRepository repo,
                                    ActivityRepository activityRepo,
                                    EquipmentRepository equipmentRepo) {
        this.repo = repo;
        this.activityRepo = activityRepo;
        this.equipmentRepo = equipmentRepo;
    }

    /**
     * Opretter en kobling mellem en aktivitet og et stykke udstyr.
     * Validerer input, sikrer at Activity/Equipment findes,
     * og at der ikke oprettes duplikat-koblinger.
     */
    @Transactional
    public ActivityEquipmentResponse create(ActivityEquipmentRequest req) {
        validate(req);

        Activity activity = activityRepo.findById(req.activityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + req.activityId()));
        Equipment equipment = equipmentRepo.findById(req.equipmentId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + req.equipmentId()));

        // Undgå at samme (activity,equipment) oprettes to gange
        if (repo.existsByActivity_IdAndEquipment_Id(req.activityId(), req.equipmentId())) {
            throw new IllegalArgumentException("ActivityEquipment already exists for activityId="
                    + req.activityId() + " and equipmentId=" + req.equipmentId());
        }

        ActivityEquipment ae = new ActivityEquipment();
        ae.setActivity(activity);
        ae.setEquipment(equipment);
        ae.setQuantity(req.quantity());

        return toResponse(repo.save(ae));
    }

    /**
     * Henter en enkelt relation på id.
     *
     * @throws IllegalArgumentException hvis relationen ikke findes
     */
    @Transactional(readOnly = true)
    public ActivityEquipmentResponse get(Long id) {
        return repo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("ActivityEquipment not found: " + id));
    }

    /**
     * Returnerer alle relationer, evt. filtreret på activityId og/eller equipmentId.
     * Hvis begge filtre er null, returneres alle poster.
     */
    @Transactional(readOnly = true)
    public List<ActivityEquipmentResponse> list(Long activityId, Long equipmentId) {
        List<ActivityEquipment> data;
        if (activityId != null && equipmentId != null) {
            data = repo.findByActivity_IdAndEquipment_Id(activityId, equipmentId)
                    .map(List::of).orElse(List.of());
        } else if (activityId != null) {
            data = repo.findByActivity_Id(activityId);
        } else if (equipmentId != null) {
            data = repo.findByEquipment_Id(equipmentId);
        } else {
            data = repo.findAll();
        }
        return data.stream().map(this::toResponse).toList();
    }

    /**
     * Opdaterer en eksisterende relation. Understøtter partial update.
     *
     * @throws IllegalArgumentException hvis id ikke findes
     */
    @Transactional
    public ActivityEquipmentResponse update(Long id, ActivityEquipmentRequest req) {
        ActivityEquipment ae = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ActivityEquipment not found: " + id));

        if (req.activityId() != null && !req.activityId().equals(ae.getActivity().getId())) {
            Activity activity = activityRepo.findById(req.activityId())
                    .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + req.activityId()));
            ae.setActivity(activity);
        }
        if (req.equipmentId() != null && !req.equipmentId().equals(ae.getEquipment().getId())) {
            Equipment equipment = equipmentRepo.findById(req.equipmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + req.equipmentId()));
            ae.setEquipment(equipment);
        }
        if (req.quantity() != null) {
            if (req.quantity() <= 0) throw new IllegalArgumentException("quantity must be > 0");
            ae.setQuantity(req.quantity());
        }

        // Tjek duplikat efter evt. ændring
        if (repo.existsByActivity_IdAndEquipment_Id(ae.getActivity().getId(), ae.getEquipment().getId())) {
            // tillad, hvis det er "samme række" som vi opdaterer
            repo.findByActivity_IdAndEquipment_Id(ae.getActivity().getId(), ae.getEquipment().getId())
                    .filter(existing -> !existing.getId().equals(ae.getId()))
                    .ifPresent(existing -> {
                        throw new IllegalArgumentException("ActivityEquipment already exists for activityId="
                                + ae.getActivity().getId() + " and equipmentId=" + ae.getEquipment().getId());
                    });
        }

        return toResponse(repo.save(ae));
    }

    /**
     * Sletter en relation.
     *
     * @throws IllegalArgumentException hvis id ikke findes
     */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("ActivityEquipment not found: " + id);
        }
        repo.deleteById(id);
    }

    // --------- Helpers ---------

    private void validate(ActivityEquipmentRequest req) {
        if (req.activityId() == null) throw new IllegalArgumentException("activityId is required");
        if (req.equipmentId() == null) throw new IllegalArgumentException("equipmentId is required");
        if (req.quantity() == null || req.quantity() <= 0)
            throw new IllegalArgumentException("quantity must be > 0");
    }

    private ActivityEquipmentResponse toResponse(ActivityEquipment ae) {
        return new ActivityEquipmentResponse(
                ae.getId(),
                ae.getActivity().getId(),
                ae.getActivity().getName(),
                ae.getEquipment().getId(),
                ae.getEquipment().getEquipmentName(),
                ae.getQuantity()
        );
    }
}
