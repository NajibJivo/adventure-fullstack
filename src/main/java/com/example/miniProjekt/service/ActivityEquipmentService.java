package com.example.miniProjekt.service;

import com.example.miniProjekt.model.ActivityEquipment;
import com.example.miniProjekt.model.ActivityEquipmentId;
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

    private ActivityEquipmentId aeId(Long activityId, Long equipmentId) {
        return new ActivityEquipmentId(activityId, equipmentId);
    }

    /**
     * Opretter kobling; fejler hvis den allerede findes.
     *
     * @param req activityId, equipmentId, quantity
     * @return oprettet kobling
     * @throws IllegalArgumentException hvis activity/equipment mangler eller quantity <= 0
     */
    @Transactional
    public ActivityEquipmentResponse create(ActivityEquipmentRequest req) {
        validate(req);

        var activity  = activityRepo.findById(req.activityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + req.activityId()));
        var equipment = equipmentRepo.findById(req.equipmentId())
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + req.equipmentId()));

        if (repo.existsByActivity_IdAndEquipment_Id(req.activityId(), req.equipmentId())) {
            throw new IllegalArgumentException("Relation already exists for activityId="
                    + req.activityId() + " and equipmentId=" + req.equipmentId());
        }

        var ae = new ActivityEquipment(activity, equipment, req.quantity());
        // settere holder EmbeddedId i sync
        return toResponse(repo.save(ae));
    }


    /**
     * Finder én kobling.
     *
     * @throws IllegalArgumentException hvis ikke fundet
     */
    @Transactional(readOnly = true)
    public ActivityEquipmentResponse get(Long activityId, Long equipmentId) {
        return repo.findById(aeId(activityId, equipmentId))
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ActivityEquipment not found: (" + activityId + "," + equipmentId + ")"));
    }


    /**
     * Lister koblinger, evt. filtreret.
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
     * Opdaterer en kobling (partial).
     *
     * @throws IllegalArgumentException hvis ikke fundet, eller hvis ny nøgle giver duplikat
     */
    @Transactional
    public ActivityEquipmentResponse update(Long activityId, Long equipmentId, ActivityEquipmentRequest req) {
        var id = aeId(activityId, equipmentId);
        var ae = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ActivityEquipment not found: (" + activityId + "," + equipmentId + ")"));

        if (req.activityId() != null && !req.activityId().equals(ae.getActivity().getId())) {
            var activity = activityRepo.findById(req.activityId())
                    .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + req.activityId()));
            ae.setActivity(activity);
        }
        if (req.equipmentId() != null && !req.equipmentId().equals(ae.getEquipment().getId())) {
            var equipment = equipmentRepo.findById(req.equipmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + req.equipmentId()));
            ae.setEquipment(equipment);
        }
        if (req.quantity() != null) {
            if (req.quantity() <= 0) throw new IllegalArgumentException("quantity must be > 0");
            ae.setQuantity(req.quantity());
        }

        // Duplikat-beskyttelse når nøgler ændres
        var newActId = ae.getActivity().getId();
        var newEqId  = ae.getEquipment().getId();
        if (!(newActId.equals(activityId) && newEqId.equals(equipmentId))
                && repo.existsByActivity_IdAndEquipment_Id(newActId, newEqId)) {
            throw new IllegalArgumentException("Relation already exists for activityId="
                    + newActId + " and equipmentId=" + newEqId);
        }

        return toResponse(repo.save(ae));
    }

    /**
     * Sletter koblingen.
     *
     * @throws IllegalArgumentException hvis ikke fundet
     */
    @Transactional
    public void delete(Long activityId, Long equipmentId) {
        var id = aeId(activityId, equipmentId);
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("ActivityEquipment not found: (" + activityId + "," + equipmentId + ")");
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
                ae.getActivity().getId(),
                ae.getActivity().getName(),          // <-- byttes op foran
                ae.getEquipment().getId(),
                ae.getEquipment().getEquipmentName(),
                ae.getQuantity()
        );
    }
}
