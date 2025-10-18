package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.SaleService;
import com.example.miniProjekt.web.dto.SaleRequest;
import com.example.miniProjekt.web.dto.SaleResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/sales")
public class SaleController {
    private final SaleService service;

    public SaleController(SaleService service) {
        this.service = service;
    }

    /** CREATE **/
    @PostMapping
    public ResponseEntity<SaleResponse> create(@RequestBody SaleRequest req) {
        SaleResponse created = service.create(req);
        URI location = URI.create("/api/sales/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    /** READ by id **/
    @GetMapping("/{id}")
    public SaleResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    /** READ all **/
    @GetMapping
    public List<SaleResponse> list() {
        return service.list();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build(); // 204
    }
}
