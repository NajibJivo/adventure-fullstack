package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Product;
import com.example.miniProjekt.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * ProductController – REST-endpoints for CRUD på produkter.
 * Sender/returnerer hele Product-objekter (ingen DTO endnu).
 */
@RestController
@RequestMapping("/product")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    /** READ ALL **/
    @GetMapping
    public List<Product> getAll() {
        return service.findAll();
    }

    /** READ BY ID **/
    @GetMapping("/{id}")
    public Product getOne(@PathVariable Long id) {
        return service.getByIdOrThrow(id);
    }

    /** CREATE **/
    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product input) {
        Product saved = service.create(input);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /** UPDATE **/
    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product input) {
        return service.update(id, input);
    }

    /** DELETE **/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
