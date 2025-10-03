package com.example.miniProjekt.controller;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.repository.ActivityRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/activities") // Base URL for all activity-related endpoints
public class ActivityController {

    private final ActivityRepository activityRepository;

    public ActivityController(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /** READ ALL **/
    @GetMapping
    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    /** READ BY ID **/
    @GetMapping("/{id}")
    public Optional<Activity> getActivityById(@PathVariable Long id) {
        return activityRepository.findById(id);
    }

    /** CREATE **/
    @PostMapping
    public Activity createActivity(@RequestBody Activity activity) {
        return activityRepository.save(activity);
    }

    /** UPDATE **/
    @PutMapping("/{id}")
    public Activity updateActivity(@PathVariable Long id, @RequestBody Activity updatedActivity) {
        return activityRepository.findById(id)
                .map(activity -> {
                    activity.setName(updatedActivity.getName());
                    activity.setDescription(updatedActivity.getDescription());
                    activity.setPrice(updatedActivity.getPrice());
                    activity.setDuration(updatedActivity.getDuration());
                    activity.setMinAge(updatedActivity.getMinAge());
                    activity.setMinHeight(updatedActivity.getMinHeight());
                    activity.setAvailableFrom(updatedActivity.getAvailableFrom());
                    activity.setAvailableTo(updatedActivity.getAvailableTo());
                    return activityRepository.save(activity);
                })
                .orElseGet(() -> { // If activity with the given ID doesn't exist, create a new one,
                                   // lambda runs only if Optional is empty due to orElseGet
                    updatedActivity.setId(id);
                    return activityRepository.save(updatedActivity);
                });
    }
}
