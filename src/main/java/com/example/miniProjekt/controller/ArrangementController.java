package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.ArrangementService;
import com.example.miniProjekt.web.dto.ArrangementRequest;
import com.example.miniProjekt.web.dto.ArrangementResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST-controller for Arrangement.
 * Ansvar: HTTP-kontrakt (routes, statuskoder) og delegering til service.
 */
@RestController
@RequestMapping("/arrangements")
public class ArrangementController {
    private final ArrangementService service;

    public ArrangementController(ArrangementService service) {
        this.service = service;
    }

    /** CREATE: POST /api/arrangements – opret nyt arrangement.
     ** @param req input-DTO
     * @return 201 Created + Location-header + response-DTO**/
    @PostMapping
    public ResponseEntity<ArrangementResponse> create(@RequestBody ArrangementRequest req) {
        var created = service.create(req);
        return ResponseEntity.created(URI.create("/api/arrangements/" + created.id())).body(created);
    }

    /** READ BY ID: GET /api/arrangements/{id} – hent enkelt arrangement. **/
    @GetMapping("/{id}")
    public ArrangementResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    /** READ ALL: GET /api/arrangements – list alle arrangementer. **/
    @GetMapping
    public List<ArrangementResponse> list() {
        return service.list();
    }

    /** UPDATE: PUT /api/arrangements/{id} – opdater (partial/full).
     * Null-felter i request ignoreres.**/
    @PutMapping("/{id}")
    public ArrangementResponse update(@PathVariable Long id, @RequestBody ArrangementRequest req) {
        return service.update(id, req);
    }

    /** DELETE (204): /api/arrangements/{id} – slet arrangement.
     *  Returnerer 204 No Content ved succes.**/
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
