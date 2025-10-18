package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.RosterService;
import com.example.miniProjekt.web.dto.RosterRequest;
import com.example.miniProjekt.web.dto.RosterResponse;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

/**
 * REST-controller for Roster (vagtplan).
 * <p>
 * Endpoints returnerer altid DTO'er ({@link RosterResponse}), ikke JPA-entities.
 * Understøtter fleksibel filtrering via query-parametre.
 **/
@RestController
@RequestMapping("api/roster")
public class RosterController {

    private final RosterService service;
    public RosterController(RosterService service) { this.service = service; }

    /** LIST (flexibel søgning) **/
    @GetMapping
    public List<RosterResponse> list(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, name = "from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false, name = "to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        if (date != null) return service.findByDate(date);
        if (from != null && to != null) return service.findByRange(from, to);
        return service.findAll();
    }




    /** CREATE (201 + Location) — customerId **/
    @PostMapping
    public ResponseEntity<RosterResponse> create(@RequestBody RosterRequest req) {
        RosterResponse created = service.create(req);
        return ResponseEntity.created(URI.create("/api/rosters/" + created.id())).body(created);
    }

    /** READ by id **/
    @GetMapping("/{id}")
    public RosterResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    /** UPDATE — valgfri customerId (null fjerner koblingen) **/
    @PutMapping("/{id}")
    public RosterResponse update(@PathVariable Long id, @RequestBody RosterRequest req) {
        return service.update(id, req);
    }

    /** DELETE (204) **/
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
