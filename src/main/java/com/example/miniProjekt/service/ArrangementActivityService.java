package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.model.Arrangement;
import com.example.miniProjekt.model.ArrangementActivity;
import com.example.miniProjekt.repository.ArrangementActivityRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArrangementActivityService {
    private final ArrangementActivityRepository aaRepo;
    private final ArrangementService arrangementService;
    private final ActivityServiceDto activityService;


    public ArrangementActivityService(ArrangementActivityRepository aaRepo,
                                      ArrangementService arrangementService,
                                      ActivityServiceDto activityService) {
        this.aaRepo = aaRepo;
        this.arrangementService = arrangementService;
        this.activityService = activityService;
    }

    /** LIST: alle aktiviteter for et arrangement */
    @Transactional(readOnly = true)
    public List<Activity> listActivitiesForArrangement(Long arrangementId) {
        return aaRepo.findByArrangement_Id(arrangementId).stream()
                .map(ArrangementActivity::getActivity)
                .toList();
    }


    /** ADD: tilføj én aktivitet til et arrangement (uden dubletter) */
    @Transactional
    public void addActivityToArrangement(Long arrangementId, Long activityId) {
        Arrangement arrangement = arrangementService.getByIdOrThrow(arrangementId);
        Activity activity = activityService.getByIdOrThrow(activityId);

        if (aaRepo.existsByArrangement_IdAndActivity_Id(arrangementId, activityId)) {
            throw new DataIntegrityViolationException("Activity already added to arrangement");
        }

        ArrangementActivity link = new ArrangementActivity(arrangement, activity);
        aaRepo.save(link);
    }

    /** REMOVE: fjern koblingen */
    @Transactional
    public void removeActivityFromArrangement(Long arrangementId, Long activityId) {
        aaRepo.deleteByArrangement_IdAndActivity_Id(arrangementId, activityId);
    }
}
