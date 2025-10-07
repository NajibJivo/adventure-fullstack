package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.service.ActivityService;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/activities") // Base URL for all activity-related endpoints
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    /** READ ALL **/
    @GetMapping
    public List<Activity> getAllActivities() {
        return activityService.findAll();
    }

    /** READ BY ID **/
    @GetMapping("/{id}")
    public Activity getActivityById(@PathVariable Long id) {
        return activityService.getByIdOrThrow(id);
    }

    /** CREATE (201 Created + Location-header) **/
    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity) {
        Activity saved = activityService.create(activity);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(saved.getId())
                .toUri();
        return ResponseEntity.created(location).body(saved);
    }

    /** UPDATE (200 OK) **/
    @PutMapping("/{id}")
    public Activity updateActivity(@PathVariable Long id, @RequestBody Activity updatedActivity) {
        return activityService.update(id, updatedActivity);
    }

    // DELETE (nyt endpoint)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        activityService.delete(id);              // kaster 404-exception hvis ikke findes
        return ResponseEntity.noContent().build(); // 204
    }
}
