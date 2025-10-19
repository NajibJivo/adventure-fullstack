package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Equipment;
import com.example.miniProjekt.repository.EquipmentRepository;
import com.example.miniProjekt.service.exceptions.EquipmentNotFoundException;
import com.example.miniProjekt.web.dto.EquipmentRequest;
import com.example.miniProjekt.web.dto.EquipmentResponse;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EquipmentService {
    private final EquipmentRepository repo;

    public EquipmentService(EquipmentRepository repo) {
        this.repo = repo;
    }

    /** CREATE (DTO in -> DTO out) **/
    @Transactional
    public EquipmentResponse create(EquipmentRequest req) {
        validate(req);
        Equipment e = new Equipment();
        apply(req, e);
        return toResponse(repo.save(e));
    }

    /** READ single **/
    @Transactional(readOnly = true)
    public EquipmentResponse get(Long id) {
        return repo.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + id));
    }

    /** READ all **/
    @Transactional(readOnly = true)
    public List<EquipmentResponse> list() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    /** UPDATE (partial) **/
    @Transactional
    public EquipmentResponse update(Long id, EquipmentRequest req) {
        Equipment e = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Equipment not found: " + id));
        apply(req, e);
        return toResponse(repo.save(e));
    }

    /** DELETE **/
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Equipment not found: " + id);
        repo.deleteById(id);
    }

    /* ====== MAPPERS ====== */

    /** Entity -> DTO (Response) **/
    private EquipmentResponse toResponse(Equipment e) {
        return new EquipmentResponse(
                e.getId(),
                e.getEquipmentName(),
                e.getMaintenanceDate()
        );
    }

    /** Request (partial) -> Entity **/
    private void apply(EquipmentRequest r, Equipment e) {
        if (r.equipmentName() != null && !r.equipmentName().isBlank()) {
            e.setEquipmentName(r.equipmentName());
        }
        if (r.maintenanceDate() != null) {
            e.setMaintenanceDate(r.maintenanceDate());
        }
    }

    /** ====== VALIDATION ====== **/

    private void validate(EquipmentRequest r) {
        if (r.equipmentName() == null || r.equipmentName().isBlank()) {
            throw new IllegalArgumentException("equipmentName is required");
        }
        // r.maintenanceDate() kan være null (OK)
    }
}
