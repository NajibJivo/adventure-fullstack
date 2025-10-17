package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.EquipmentService;
import com.example.miniProjekt.web.dto.EquipmentRequest;
import com.example.miniProjekt.web.dto.EquipmentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/equipment")
public class EquipmentController {
    private final EquipmentService service;

    public EquipmentController(EquipmentService service) {
        this.service = service;
    }


    /** CREATE (201 + Location) **/
    @PostMapping
    public ResponseEntity<EquipmentResponse> create(@RequestBody EquipmentRequest req) {
        var created = service.create(req);
        return ResponseEntity.created(URI.create("api/equipment" + created.id())).body(created);
    }

    /** READ ALL **/
    @GetMapping
    public List<EquipmentResponse> list() { return service.list(); }

    /** READ BY ID **/
    @GetMapping("/{id}")
    public EquipmentResponse getById(@PathVariable Long id) { return service.get(id); }


    /** UPDATE **/
    @PutMapping("/{id}")
    public EquipmentResponse update(@PathVariable Long id, @RequestBody EquipmentRequest req) {
        return service.update(id, req);
    }

    /** DELETE (204) **/
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
