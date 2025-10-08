package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Customer;
import com.example.miniProjekt.repository.CustomerRepository;
import com.example.miniProjekt.web.dto.CustomerRequest;
import com.example.miniProjekt.web.dto.CustomerResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerRepository repo;

    public CustomerService(CustomerRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public CustomerResponse create(CustomerRequest req) {
        if (repo.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already in use: " + req.email());
        }
        Customer c = new Customer();
        c.setName(req.name());
        c.setPhone(req.phone());
        c.setEmail(req.email());
        c.setUserRole(req.userRole());
        return toResponse(repo.save(c));
    }

    @Transactional(readOnly = true)
    public CustomerResponse get(Long id) {
        return repo.findById(id).map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> list() {
        return repo.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public CustomerResponse update(Long id, CustomerRequest req) {
        Customer c = repo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));

        if (req.name() != null) c.setName(req.name());
        if (req.phone() != null) c.setPhone(req.phone());
        if (req.email() != null && !req.email().equals(c.getEmail())) {
            if (repo.existsByEmail(req.email())) {
                throw new IllegalArgumentException("Email already in use: " + req.email());
            }
            c.setEmail(req.email());
        }
        if (req.userRole() != null) c.setUserRole(req.userRole());

        return toResponse(repo.save(c));
    }

    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("Customer not found: " + id);
        }
        repo.deleteById(id);
    }

    private CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(c.getId(), c.getName(), c.getPhone(), c.getEmail(), c.getUserRole());
    }
}
