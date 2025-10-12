package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Customer;
import com.example.miniProjekt.model.Roster;
import com.example.miniProjekt.repository.RosterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RosterService {
    private final RosterRepository repo;
    private final CustomerService customerService; // til  FK-assign til Customer

    public RosterService(RosterRepository repo, CustomerService customerService) {
        this.repo = repo;
        this.customerService = customerService;
    }

    // READ
    public List<Roster> findAll() { return repo.findAll(); }
    public List<Roster> findByDate(LocalDate date) { return repo.findByWorkDate(date); }
    public List<Roster> findByRange(LocalDate from, LocalDate to) { return repo.findByWorkDateBetween(from, to); }
    public List<Roster> findByInstructor(String name) { return repo.findByInstructorNameIgnoreCase(name); }

    // CREATE
    @Transactional
    public Roster create(Roster input, Long customerIdOrNull) {
        validate(input);
        input.setId(null);
        if (customerIdOrNull != null) {
            Customer c = customerService.getByIdOrThrow(customerIdOrNull);
            input.setCustomer(c);
        }
        return repo.save(input);
    }

    // UPDATE (skift navn/dato og evt. tilknyttet kunde)
    @Transactional
    public Roster update(Long id, Roster input, Long customerIdOrNull) {
        validate(input);
        Roster existing = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Roster not found: id=" + id));
        existing.setInstructorName(input.getInstructorName());
        existing.setWorkDate(input.getWorkDate());
        if (customerIdOrNull != null) {
            Customer c = customerService.getByIdOrThrow(customerIdOrNull);
            existing.setCustomer(c);
        } else {
            existing.setCustomer(null);
        }
        return repo.save(existing);
    }

    // DELETE
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Roster not found: id=" + id);
        repo.deleteById(id);
    }

    private void validate(Roster r) {
        if (r == null) throw new IllegalArgumentException("Roster is required");
        if (r.getInstructorName() == null || r.getInstructorName().isBlank())
            throw new IllegalArgumentException("instructorName is required");
        if (r.getInstructorName().length() > 100)
            throw new IllegalArgumentException("instructorName max 100 chars");
        if (r.getWorkDate() == null)
            throw new IllegalArgumentException("workDate is required");
    }
}
