package com.example.miniProjekt.Controller;

import com.example.miniProjekt.Service.ActivityService;
import com.example.miniProjekt.entity.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/activities")
@CrossOrigin(origins = "*")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    @GetMapping
    public List<Activity> getAllActivities() {
        return activityService.getAllActivities();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Activity> getActivityById(@PathVariable Long id) {
        return activityService.getActivityById(id)
                .map(activity -> ResponseEntity.ok().body(activity))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Activity createActivity(@RequestBody Activity activity) {
        return activityService.saveActivity(activity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity activityDetails) {
        return activityService.getActivityById(id)
                .map(activity -> {
                    activity.setName(activityDetails.getName());
                    activity.setDescription(activityDetails.getDescription());
                    activity.setMinAge(activityDetails.getMinAge());
                    activity.setMaxParticipants(activityDetails.getMaxParticipants());
                    activity.setDurationMinutes(activityDetails.getDurationMinutes());
                    activity.setPrice(activityDetails.getPrice());
                    activity.setEquipmentRequired(activityDetails.getEquipmentRequired());
                    return ResponseEntity.ok(activityService.saveActivity(activity));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteActivity(@PathVariable Long id) {
        return activityService.getActivityById(id)
                .map(activity -> {
                    activityService.deleteActivity(id);
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Specialiserede endpoints
    @GetMapping("/for-age/{age}")
    public List<Activity> getActivitiesForAge(@PathVariable Integer age) {
        return activityService.getActivitiesForAge(age);
    }

    @GetMapping("/suitable")
    public List<Activity> getSuitableActivities(
            @RequestParam Integer age,
            @RequestParam Integer participants) {
        return activityService.getSuitableActivities(age, participants);
    }

    @PostMapping("/init-data")
    public ResponseEntity<String> initializeTestData() {
        activityService.initializeTestData();
        return ResponseEntity.ok("Test data initialized");
    }
}
