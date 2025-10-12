package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Equipment;
import com.example.miniProjekt.repository.EquipmentRepository;
import com.example.miniProjekt.service.exceptions.EquipmentNotFoundException;
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

    /* READ */
    public List<Equipment> findAll() { return repo.findAll(); }

    public Equipment getByIdOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new EquipmentNotFoundException(id));
    }

    /* CREATE */
    @Transactional
    public Equipment create(Equipment input) {
        validate(input);
        input.setId(null);
        if (repo.existsByNameIgnoreCase(input.getName())) {
            throw new DataIntegrityViolationException("Equipment name must be unique");
        }
        return repo.save(input);
    }

    /* UPDATE */
    @Transactional
    public Equipment update(Long id, Equipment input) {
        validate(input);
        Equipment existing = getByIdOrThrow(id);
        // tjek unikhed hvis navnet ændres
        if (!existing.getName().equalsIgnoreCase(input.getName())
                && repo.existsByNameIgnoreCase(input.getName())) {
            throw new DataIntegrityViolationException("Equipment name must be unique");
        }
        existing.setName(input.getName());
        existing.setMaintenanceDate(input.getMaintenanceDate());
        return repo.save(existing);
    }

    /* DELETE */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new EquipmentNotFoundException(id);
        repo.deleteById(id);
    }

    /* --- helpers --- */
    private void validate(Equipment e) {
        if (e == null) throw new IllegalArgumentException("Equipment is required");
        if (e.getName() == null || e.getName().isBlank())
            throw new IllegalArgumentException("Equipment name is required");
        if (e.getName().length() > 100)
            throw new IllegalArgumentException("Equipment name max 100 chars");
        // maintenanceDate er valgfri jf. ERD
    }
}
