package com.example.miniProjekt.Service;


import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.Repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;


@Service
public class ActivityService {
    private final ActivityRepository activityRepository;

    // Constructor injection
    public ActivityService(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    /** READ **/
    public List<Activity> findAll() {
        return activityRepository.findAll();
    }

    /** READ **/
    public Optional<Activity> findById(Long id) {
        return activityRepository.findById(id);
    }









}
