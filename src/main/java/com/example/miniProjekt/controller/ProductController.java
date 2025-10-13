package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.ProductServiceDto;
import com.example.miniProjekt.web.dto.ProductRequest;
import com.example.miniProjekt.web.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

/**
 * ProductController – REST-endpoints for CRUD på produkter.
 * Sender/returnerer udelukkende DTO'er.
 * API-kontrakt; DTO’er giver frihed til at ændre domain-modeller uden at bryde klienter.
 */
@RestController
@RequestMapping("/api/product")
public class ProductController {
    private final ProductServiceDto service;

    public ProductController(ProductServiceDto service) {
        this.service = service;
    }

    /** READ ALL **/
    @GetMapping
    public Page<ProductResponse> list(@PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return service.list(pageable);
    }

    /** READ BY ID **/
    @GetMapping("/{id}")
    public ProductResponse getOne(@PathVariable Long id) {
        return service.get(id);
    }

    /** CREATE **/
    @PostMapping
    public ResponseEntity<ProductResponse> create(@RequestBody ProductRequest req) {
        ProductResponse saved = service.create(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /** UPDATE **/
    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @RequestBody ProductRequest req) {
        return service.update(id, req);
    }

    /** DELETE **/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
