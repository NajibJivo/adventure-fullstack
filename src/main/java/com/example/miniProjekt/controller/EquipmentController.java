package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Equipment;
import com.example.miniProjekt.service.EquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {
    private final EquipmentService service;

    public EquipmentController(EquipmentService service) {
        this.service = service;
    }


    /** READ ALL **/
    @GetMapping
    public List<Equipment> getAll() { return service.findAll(); }

    /** READ BY ID **/
    @GetMapping("/{id}")
    public Equipment getById(@PathVariable Long id) { return service.getByIdOrThrow(id); }

    /** CREATE (201 + Location) **/
    @PostMapping
    public ResponseEntity<Equipment> create(@RequestBody Equipment input) {
        Equipment saved = service.create(input);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /** UPDATE **/
    @PutMapping("/{id}")
    public Equipment update(@PathVariable Long id, @RequestBody Equipment input) {
        return service.update(id, input);
    }

    /** DELETE (204) **/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
