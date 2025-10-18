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
 * REST-API for koblingen mellem Activity og Equipment.
 * Path er scoped til én aktivitet: /activities/{activityId}/equipment
 */
@RestController
@RequestMapping("/activities/{activityId}/equipment")
public class ActivityEquipmentController {
    private final ActivityEquipmentService service;

    public ActivityEquipmentController(ActivityEquipmentService service) {
        this.service = service;
    }


    /** LIST alle equipment for en activity (evt. filtrér på equipmentId)
     *    * Returnerer alle koblinger for en aktivitet. */
    @GetMapping
    public List<ActivityEquipmentResponse> list(@PathVariable Long activityId,
                                                @RequestParam(required = false) Long equipmentId) {
        return service.list(activityId, equipmentId);
    }

    /** CREATE – body indeholder equipmentId + quantity
     *  Opretter en kobling mellem aktivitet og udstyr */
    @PostMapping
    public ResponseEntity<ActivityEquipmentResponse> create(@PathVariable Long activityId,
                                                            @RequestBody ActivityEquipmentRequest req) {
        // sikr at activityId i body matcher path (eller sæt det)
        if (req.activityId() == null || !req.activityId().equals(activityId)) {
            req = new ActivityEquipmentRequest(activityId, req.equipmentId(), req.quantity());
        }
        ActivityEquipmentResponse created = service.create(req);
        URI location = URI.create(String.format(
                "/activities/%d/equipment/%d", created.activityId(), created.equipmentId()
        ));
        return ResponseEntity.created(location).body(created);
    }

    /** READ én relation (komposit nøgle i path)
     *    * Henter én kobling identificeret af komposit nøgle. */
    @GetMapping("/{equipmentId}")
    public ActivityEquipmentResponse get(@PathVariable Long activityId,
                                         @PathVariable Long equipmentId) {
        return service.get(activityId, equipmentId);
    }

    /** UPDATE (komposit nøgle i path)
     * Opdaterer quantity eller skifter udstyr/aktivitet (partial update tilladt).
     */
    @PutMapping("/{equipmentId}")
    public ActivityEquipmentResponse update(@PathVariable Long activityId,
                                            @PathVariable Long equipmentId,
                                            @RequestBody ActivityEquipmentRequest req) {
        return service.update(activityId, equipmentId, req);
    }

    /** DELETE (komposit nøgle i path)
     *    * Sletter koblingen.*/
    @DeleteMapping("/{equipmentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long activityId,
                       @PathVariable Long equipmentId) {
        service.delete(activityId, equipmentId);
    }
}
