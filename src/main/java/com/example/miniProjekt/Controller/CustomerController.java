package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.CustomerService;
import com.example.miniProjekt.web.dto.CustomerRequest;
import com.example.miniProjekt.web.dto.CustomerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
/**
 * CustomerController – REST-endpoints for CRUD på kunder.
 * Benytter DTO’er (CustomerRequest og CustomerResponse).
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService service;

    public CustomerController(CustomerService service) {
        this.service = service;
    }

    /** CREATE - POST /api/customers – Opret ny kunde (201 Created + response-DTO). **/
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerResponse create(@RequestBody CustomerRequest req) {
        return service.create(req);
    }

    /** READ Single: GET /api/customers/{id} – Returnér kunde med matching id (404 hvis ikke fundet). **/
    @GetMapping("/{id}")
    public CustomerResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    /** READ ALL: GET /api/customers – Returnér liste af alle kunder som DTO’er. */
    @GetMapping
    public List<CustomerResponse> list() {
        return service.list();
    }

    /** UPDATE: PUT /api/customers/{id} – Opdater eksisterende kunde (partial update understøttes). **/
    @PutMapping("/{id}")
    public CustomerResponse update(@PathVariable Long id, @RequestBody CustomerRequest req) {
        return service.update(id, req);
    }

    /** DELETE: /api/customers/{id} – Slet kunde (204 No Content).  **/
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
