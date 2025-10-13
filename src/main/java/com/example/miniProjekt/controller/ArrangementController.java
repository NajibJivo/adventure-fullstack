package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Arrangement;
import com.example.miniProjekt.service.ArrangementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/arrangements")
public class ArrangementController {
    private final ArrangementService service;

    public ArrangementController(ArrangementService service) {
        this.service = service;
    }


    /** READ ALL **/
    @GetMapping
    public List<Arrangement> getAll() { return service.findAll();  }

    /** READ BY ID **/
    @GetMapping("/{id}")
    public Arrangement getById(@PathVariable Long id) {
        return service.getByIdOrThrow(id);
    }

    /** CREATE (201 + Location) **/
    @PostMapping
    public ResponseEntity<Arrangement> create(@RequestBody Arrangement input) {
        Arrangement saved = service.create(input);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /** UPDATE **/
    @PutMapping("/{id}")
    public Arrangement update(@PathVariable Long id, @RequestBody Arrangement input) {
        return service.update(id, input);
    }

    /** DELETE (204) **/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
