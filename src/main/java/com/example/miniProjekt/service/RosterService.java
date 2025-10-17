package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Customer;
import com.example.miniProjekt.model.Roster;
import com.example.miniProjekt.model.UserRole;
import com.example.miniProjekt.repository.CustomerRepository;
import com.example.miniProjekt.repository.RosterRepository;
import com.example.miniProjekt.web.dto.RosterRequest;
import com.example.miniProjekt.web.dto.RosterResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RosterService {
    private final RosterRepository repo;
    private final CustomerRepository customerRepo;

    public RosterService(RosterRepository repo,
                         CustomerRepository customerRepo) {
        this.repo = repo;
        this.customerRepo = customerRepo;
    }

    /** READ:
     * Hent alle roster-poster
     *  Hent roster-poster for en specifik dato.
     *  Hent roster-poster i et datointerval (inklusive grænser).
     * **/
    @Transactional(readOnly = true)
    public List<RosterResponse> findAll() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RosterResponse> findByDate(LocalDate date) {
        return repo.findByWorkDate(date).stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<RosterResponse> findByRange(LocalDate from, LocalDate to) {
        return repo.findByWorkDateBetween(from, to).stream().map(this::toResponse).toList();
    }




    /** CREATE **/
    @Transactional
    public RosterResponse create(RosterRequest req) {
        validate(req);

        Customer emp = customerRepo.findById(req.employeeId())
                .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + req.employeeId()));
        if (emp.getUserRole() != UserRole.EMPLOYEE) {
            throw new IllegalArgumentException("User is not an EMPLOYEE: " + req.employeeId());
        }

        if (repo.existsByEmployee_IdAndWorkDate(req.employeeId(), req.workDate())) {
            throw new IllegalArgumentException("Roster already exists for employee/date");
        }

        Roster r = new Roster();
        r.setCustomer(emp);
        r.setWorkDate(req.workDate());


        return toResponse(repo.save(r));
    }

    /** READ single **/
    @Transactional(readOnly = true)
    public RosterResponse get(Long id) {
        return repo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Roster not found: " + id));
    }

    /** READ all **/
    @Transactional(readOnly = true)
    public List<RosterResponse> list() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }



    /** UPDATE (skift navn/dato og evt. tilknyttet kunde) **/
    @Transactional
    public RosterResponse update(Long id, RosterRequest req) {
        Roster r = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Roster not found: " + id));

        // Skift medarbejder (valider rolle)
        if (req.employeeId() != null) {
            Customer emp = customerRepo.findById(req.employeeId())
                    .orElseThrow(() -> new IllegalArgumentException("Employee not found: " + req.employeeId()));
            if (emp.getUserRole() != UserRole.EMPLOYEE) {
                throw new IllegalArgumentException("User is not an EMPLOYEE: " + req.employeeId());
            }
            r.setCustomer(emp);
        }

        if (req.workDate() != null) {
            // Tjek unikt constraint når employee+date ændres
            Long empId = r.getCustomer() != null ? r.getCustomer().getId() : req.employeeId();
            Long checkEmpId = empId != null ? empId : req.employeeId();
            if (checkEmpId != null && repo.existsByEmployee_IdAndWorkDate(checkEmpId, req.workDate())) {
                throw new IllegalArgumentException("Roster already exists for employee/date");
            }
            r.setWorkDate(req.workDate());
        }
        return toResponse(repo.save(r));
    }

    /** DELETE **/
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) throw new IllegalArgumentException("Roster not found: " + id);
        repo.deleteById(id);
    }

    /** --- mapping & validation --- **/
    private RosterResponse toResponse(Roster r) {
        return new RosterResponse(
                r.getId(),
                r.getCustomer().getId(),
                r.getWorkDate()
        );
    }

    private void validate(RosterRequest req) {
        if (req.employeeId() == null) throw new IllegalArgumentException("employeeId is required");
        if (req.workDate() == null) throw new IllegalArgumentException("workDate is required");
        if (req.workDate().isBefore(LocalDate.now()))
            throw new IllegalArgumentException("workDate must be today or in the future");
    }
}
