package com.example.miniProjekt.controller;

import com.example.miniProjekt.service.ActivityEquipmentService;
import com.example.miniProjekt.web.dto.ActivityEquipmentRequest;
import com.example.miniProjekt.web.dto.ActivityEquipmentResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST-controller for Activity–Equipment relationen.
 * <p>
 * Modtager/returnerer DTO'er og delegere al logik til {@link ActivityEquipmentService}.
 */
@RestController
@RequestMapping("/activities/{activityId}/equipment")
public class ActivityEquipmentController {
    private final ActivityEquipmentService service;

    public ActivityEquipmentController(ActivityEquipmentService service) {
        this.service = service;
    }


    /**
     * Opretter en ny relation mellem en aktivitet og et stykke udstyr.
     *
     * @param req DTO med activityId, equipmentId og quantity
     * @return 201 Created + den oprettede ressource
     */
    @PostMapping
    public ResponseEntity<ActivityEquipmentResponse> create(@RequestBody ActivityEquipmentRequest req) {
        ActivityEquipmentResponse created = service.create(req);
        URI location = URI.create("/api/activity-equipment/" + created.id());
        return ResponseEntity.created(location).body(created);
    }

    /**
     * Henter én relation på id.
     */
    @GetMapping("/{id}")
    public ActivityEquipmentResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    /**
     * Liste-endpoint med enkle filtre:
     * - ?activityId=... for at filtrere på aktivitet
     * - ?equipmentId=... for at filtrere på udstyr
     * Uden filtre returneres alle relationer.
     */
    @GetMapping
    public List<ActivityEquipmentResponse> list(
            @RequestParam(required = false) Long activityId,
            @RequestParam(required = false) Long equipmentId
    ) {
        return service.list(activityId, equipmentId);
    }

    /**
     * Opdaterer en eksisterende relation (partial update er tilladt).
     */
    @PutMapping("/{id}")
    public ActivityEquipmentResponse update(@PathVariable Long id,
                                            @RequestBody ActivityEquipmentRequest req) {
        return service.update(id, req);
    }

    /**
     * Sletter en relation.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
