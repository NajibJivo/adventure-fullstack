package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Roster;
import com.example.miniProjekt.service.RosterService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/roster")
public class RosterController {

    private final RosterService service;
    public RosterController(RosterService service) { this.service = service; }

    /** LIST (flexibel søgning) **/
    @GetMapping
    public List<Roster> list(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false, name = "from")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false, name = "to")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) String instructor
    ) {
        if (date != null) return service.findByDate(date);
        if (from != null && to != null) return service.findByRange(from, to);
        if (instructor != null && !instructor.isBlank()) return service.findByInstructor(instructor);
        return service.findAll();
    }

    /** CREATE (201 + Location) — customerId **/
    @PostMapping
    public ResponseEntity<Roster> create(@RequestBody Roster input,
                                         @RequestParam(required = false) Long customerId) {
        Roster saved = service.create(input, customerId);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(saved.getId()).toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /** UPDATE — valgfri customerId (null fjerner koblingen) **/
    @PutMapping("/{id}")
    public Roster update(@PathVariable Long id,
                         @RequestBody Roster input,
                         @RequestParam(required = false) Long customerId) {
        return service.update(id, input, customerId);
    }

    /** DELETE (204) **/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
