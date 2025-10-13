package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Arrangement;
import com.example.miniProjekt.repository.ArrangementRepository;
import com.example.miniProjekt.service.exceptions.ArrangementNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArrangementService {
    private final ArrangementRepository repo;

    public ArrangementService(ArrangementRepository repo) {
        this.repo = repo;
    }

    /** READ */
    public List<Arrangement> findAll() { return repo.findAll(); }

    public Arrangement getByIdOrThrow(Long id) {
        return repo.findById(id).orElseThrow(() -> new ArrangementNotFoundException(id));
    }

    /** CREATE */
    @Transactional
    public Arrangement create(Arrangement input) {
        validate(input);
        input.setId(null);
        if (repo.existsByNameIgnoreCase(input.getName())) {
            throw new DataIntegrityViolationException("Arrangement.name must be unique");
        }
        return repo.save(input);
    }

    /** UPDATE */
    @Transactional
    public Arrangement update(Long id, Arrangement input) {
        validate(input);
        Arrangement existing = getByIdOrThrow(id);
        if (!existing.getName().equalsIgnoreCase(input.getName())
                && repo.existsByNameIgnoreCase(input.getName())) {
            throw new DataIntegrityViolationException("Arrangement.name must be unique");
        }
        existing.setName(input.getName());
        existing.setStartDateTime(input.getStartDateTime());
        existing.setParticipants(input.getParticipants());
        return repo.save(existing);
    }

    /** DELETE */
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new ArrangementNotFoundException(id);
        repo.deleteById(id);
    }

    /* --- helpers --- */
    private void validate(Arrangement a) {
        if (a == null) throw new IllegalArgumentException("Arrangement is required");
        if (a.getName() == null || a.getName().isBlank())
            throw new IllegalArgumentException("Arrangement.name is required");
        if (a.getName().length() > 120)
            throw new IllegalArgumentException("Arrangement.name max 120 chars");
        if (a.getStartDateTime() == null)
            throw new IllegalArgumentException("startDateTime is required");
        if (a.getParticipants() <= 0)
            throw new IllegalArgumentException("participants must be > 0");
    }
}
