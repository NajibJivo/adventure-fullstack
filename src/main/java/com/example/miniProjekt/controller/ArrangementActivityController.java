package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.ArrangementActivityService;
import com.example.miniProjekt.web.dto.ArrangementActivityRequest;
import com.example.miniProjekt.web.dto.ArrangementActivityResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/arrangements/{arrangementId}/activities")
public class ArrangementActivityController {
    private final ArrangementActivityService service;


    public ArrangementActivityController(ArrangementActivityService service) {
        this.service = service;
    }

    /** POST: tilføj aktivitet til arrangement. */
    @PostMapping
    public ResponseEntity<ArrangementActivityResponse> add(@PathVariable Long arrangementId,
                                                           @RequestBody ArrangementActivityRequest req) {
        var created = service.addActivity(arrangementId, req);
        var location = URI.create("/api/arrangements/%d/activities/%d"
                .formatted(created.arrangementId(), created.activityId()));
        return ResponseEntity.created(location).body(created);
    }

    /** GET: list aktiviteter for arrangement. */
    @GetMapping
    public List<ArrangementActivityResponse> list(@PathVariable Long arrangementId) {
        return service.list(arrangementId);
    }

    /** DELETE: fjern aktivitet fra arrangement. */
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> remove(@PathVariable Long arrangementId,
                                       @PathVariable Long activityId) {
        service.remove(arrangementId, activityId);
        return ResponseEntity.noContent().build();
    }
}
