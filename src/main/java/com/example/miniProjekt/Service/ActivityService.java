package com.example.miniProjekt.Service;

import com.example.miniProjekt.Repository.ActivityRepository;
import com.example.miniProjekt.Model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    private ActivityRepository activityRepository;

    public List<Activity> getAllActivities() {
        return activityRepository.findAll();
    }

    public Optional<Activity> getActivityById(Long id) {
        return activityRepository.findById(id);
    }

    public Activity saveActivity(Activity activity) {
        return activityRepository.save(activity);
    }

    public void deleteActivity(Long id) {
        activityRepository.deleteById(id);
    }

    public List<Activity> getActivitiesForAge(Integer age) {
        return activityRepository.findByMinAgeLessThanEqual(age);
    }

    public List<Activity> getActivitiesWithEquipment() {
        return activityRepository.findByEquipmentRequiredTrue();
    }

    public List<Activity> getSuitableActivities(Integer age, Integer participants) {
        return activityRepository.findSuitableActivities(age, participants);
    }

    // Initialiser test data
    public void initializeTestData() {
        if (activityRepository.count() == 0) {
            Activity gocart = new Activity("Go-kart", "Spændende go-kart bane", 12, 8, 15, 150.0, true);
            Activity minigolf = new Activity("Minigolf", "18 hullers minigolf bane", 5, 10, 60, 75.0, false);
            Activity paintball = new Activity("Paintball", "Taktisk paintball kamp", 16, 12, 90, 200.0, true);
            Activity sumo = new Activity("Sumo Wrestling", "Sjov sumo brydning", 8, 4, 20, 100.0, true);

            activityRepository.saveAll(List.of(gocart, minigolf, paintball, sumo));
        }
    }
}
