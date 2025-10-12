package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Equipment;
import com.example.miniProjekt.service.ActivityEquipmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activities/{activityId}/equipment")
public class ActivityEquipmentController {
    private final ActivityEquipmentService service;

    public ActivityEquipmentController(ActivityEquipmentService service) {
        this.service = service;
    }

    /** LIST: alt udstyr på en aktivitet */
    @GetMapping
    public List<Equipment> list(@PathVariable Long activityId) {
        return service.listEquipmentForActivity(activityId);
    }

    /** ADD: tilføj et udstyr til en aktivitet (idempotent mht. dubletter) */
    @PostMapping("/{equipmentId}")
    public ResponseEntity<Void> add(@PathVariable Long activityId,
                                    @PathVariable Long equipmentId) {
        service.addEquipmentToActivity(activityId, equipmentId);
        return ResponseEntity.noContent().build(); // 204
    }

    /** REMOVE: fjern koblingen */
    @DeleteMapping("/{equipmentId}")
    public ResponseEntity<Void> remove(@PathVariable Long activityId,
                                       @PathVariable Long equipmentId) {
        service.removeEquipmentFromActivity(activityId, equipmentId);
        return ResponseEntity.noContent().build(); // 204
    }
}
