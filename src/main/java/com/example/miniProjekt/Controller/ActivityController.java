package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.service.ActivityServiceDto;


import com.example.miniProjekt.web.dto.ActivityRequest;
import com.example.miniProjekt.web.dto.ActivityResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;


@RestController
@RequestMapping("/api/activities") // Base URL for all activity-related endpoints
public class ActivityController {

    private final ActivityServiceDto service;

    public ActivityController(ActivityServiceDto service) {
        this.service = service;
    }

    /** READ ALL **/
    @GetMapping
    public Page<ActivityResponse> getAllActivities(
            @PageableDefault(size = 20, sort = "name") Pageable pageable) {

        return service.list(pageable);
    }

    /** READ BY ID **/
    @GetMapping("/{id}")
    public Activity getActivityById(@PathVariable Long id) {
        return service.getByIdOrThrow(id);
    }

    /** CREATE (201 Created + Location-header) **/
    @PostMapping
    public ResponseEntity<ActivityResponse> create(@RequestBody ActivityRequest req) {
        ActivityResponse saved = service.create(req);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.id())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }


    /** UPDATE (200 OK) **/
    @PutMapping("/{id}")
    public ActivityResponse updateActivity(@PathVariable Long id, @RequestBody ActivityRequest req) {
        return service.update(id, req);
    }

    /** DELETE (nyt endpoint) **/
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);              // kaster 404-exception hvis ikke findes
        return ResponseEntity.noContent().build(); // 204
    }

    /**  Hvorfor?
     *   - Standardiseret API: frontend kan styre side-størrelse og sortering via URL.
     *   - Ensartet respons: Page<T> giver indhold + totaler, som er nemt at bruge i UI.
     **/
    @GetMapping("/search")
    public Page<ActivityResponse> search(@RequestParam String q, @PageableDefault(size = 20) Pageable pageable) {
        return service.search(q, pageable);
    }


}
