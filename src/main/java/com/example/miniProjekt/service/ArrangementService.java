package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Arrangement;
import com.example.miniProjekt.model.Customer;
import com.example.miniProjekt.repository.ArrangementRepository;
import com.example.miniProjekt.repository.CustomerRepository;
import com.example.miniProjekt.web.dto.ArrangementRequest;
import com.example.miniProjekt.web.dto.ArrangementResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/**
 * Service-lag for Arrangement
 * Ansvar: forretningsregler + CRUD, mapping mellem enity og DTO.
 * **/
@Service
public class ArrangementService {
    private final ArrangementRepository repo;
    private final CustomerRepository customerRepo;

    public ArrangementService(ArrangementRepository repo,
                              CustomerRepository customerRepo) {
        this.repo = repo;
        this.customerRepo = customerRepo;
    }

    /** CREATE: Opretter et nyt arrangement.
     * Validerer input, slår customer op og persisterer entity. **/
    @Transactional
    public ArrangementResponse create(ArrangementRequest req) {
        validate(req);

        Customer customer = customerRepo.findById(req.customerId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.customerId()));

        Arrangement a = new Arrangement();
        a.setCustomer(customer);
        a.setTitle(req.title());
        a.setEventDate(req.eventDate());
        a.setNotes(req.notes());
        a.setMaxParticipants(req.maxParticipants());

        return toResponse(repo.save(a));
    }


    /** READ single:  Henter et arrangement pr. id. **/
    @Transactional(readOnly = true)
    public ArrangementResponse get(Long id) {
        return repo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Arrangement not found: " + id));
    }

    /** READ all: Returnerer alle arrangementer. **/
    @Transactional(readOnly = true)
    public List<ArrangementResponse> list() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }


    /** UPDATE (partial): Opdaterer et arrangement (fuldt eller delvist). **/
    @Transactional
    public ArrangementResponse update(Long id, ArrangementRequest req) {
        Arrangement a = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Arrangement not found: " + id));

        if (req.customerId() != null) {
            Customer customer = customerRepo.findById(req.customerId())
                    .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + req.customerId()));
            a.setCustomer(customer);
        }
        if (req.title() != null && !req.title().isBlank()) a.setTitle(req.title());
        if (req.eventDate() != null) a.setEventDate(req.eventDate());
        if (req.notes() != null) a.setNotes(req.notes());
        if (req.maxParticipants() != null) a.setMaxParticipants(req.maxParticipants());

        return toResponse(repo.save(a));
    }

    /** DELETE: Sletter et arrangement pr. id. **/
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Arrangement not found: " + id);
        repo.deleteById(id);
    }


    /** === Mapping  === */
    private ArrangementResponse toResponse(Arrangement a) {
        return new ArrangementResponse(
                a.getId(),
                a.getCustomer().getId(),
                a.getTitle(),
                a.getEventDate(),
                a.getNotes(),
                a.getMaxParticipants()
        );
    }

    /** ===== VALIDATION ===== **/
    private void validate(ArrangementRequest r) {
        if (r.customerId() == null) throw new IllegalArgumentException("customerId is required");
        if (r.title() == null || r.title().isBlank()) throw new IllegalArgumentException("title is required");
        if (r.eventDate() == null) throw new IllegalArgumentException("eventDate is required");
        // maxParticipants er valgfri i ERD → ingen hard check.
    }
}
