package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.service.ArrangementActivityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/arrangements/{arrangementId}/activities")
public class ArrangementActivityController {
    private final ArrangementActivityService service;


    public ArrangementActivityController(ArrangementActivityService service) {
        this.service = service;
    }

    /** LIST: alle aktiviteter på et arrangement */
    @GetMapping
    public List<Activity> list(@PathVariable Long arrangementId) {
        return service.listActivitiesForArrangement(arrangementId);
    }

    /** ADD: tilføj en aktivitet til arrangementet */
    @PostMapping("/{activityId}")
    public ResponseEntity<Void> add(@PathVariable Long arrangementId,
                                    @PathVariable Long activityId) {
        service.addActivityToArrangement(arrangementId, activityId);
        return ResponseEntity.noContent().build(); // 204
    }

    /** REMOVE: fjern en aktivitet fra arrangementet */
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> remove(@PathVariable Long arrangementId,
                                       @PathVariable Long activityId) {
        service.removeActivityFromArrangement(arrangementId, activityId);
        return ResponseEntity.noContent().build(); // 204
    }

}
