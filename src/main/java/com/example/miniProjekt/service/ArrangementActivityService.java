package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.model.Arrangement;
import com.example.miniProjekt.model.ArrangementActivity;
import com.example.miniProjekt.repository.ActivityRepository;
import com.example.miniProjekt.repository.ArrangementActivityRepository;
import com.example.miniProjekt.repository.ArrangementRepository;
import com.example.miniProjekt.web.dto.ArrangementActivityRequest;
import com.example.miniProjekt.web.dto.ArrangementActivityResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
/** Forretningslogik for M:N link mellem Arrangement og Activity. */
@Service
public class ArrangementActivityService {
    private final ArrangementActivityRepository repo;
    private final ArrangementRepository arrangementRepo;
    private final ActivityRepository activityRepo;

    public ArrangementActivityService(ArrangementActivityRepository repo,
                                      ArrangementRepository arrangementRepo,
                                      ActivityRepository activityRepo) {
        this.repo = repo;
        this.arrangementRepo = arrangementRepo;
        this.activityRepo = activityRepo;
    }

    /** Tilføj aktivitet til arrangement. */
    @Transactional
    public ArrangementActivityResponse addActivity(Long arrangementId, ArrangementActivityRequest req) {
        Arrangement arrangement = arrangementRepo.findById(arrangementId)
                .orElseThrow(() -> new IllegalArgumentException("Arrangement not found: " + arrangementId));
        Activity activity = activityRepo.findById(req.activityId())
                .orElseThrow(() -> new IllegalArgumentException("Activity not found: " + req.activityId()));

        if (repo.existsByArrangement_IdAndActivity_Id(arrangementId, req.activityId())) {
            throw new IllegalArgumentException("Activity already linked to arrangement");
        }

        ArrangementActivity link = new ArrangementActivity(arrangement, activity);
        link = repo.save(link);
        return new ArrangementActivityResponse(
                link.getArrangement().getId(),
                link.getActivity().getId(),
                link.getActivity().getName()
        );
    }

    /** List aktiviteter for et arrangement. */
    @Transactional(readOnly = true)
    public List<ArrangementActivityResponse> list(Long arrangementId) {
        // valgfrit: check at arrangement findes
        if (!arrangementRepo.existsById(arrangementId)) {
            throw new IllegalArgumentException("Arrangement not found: " + arrangementId);
        }
        return repo.findByArrangement_Id(arrangementId).stream()
                .map(l -> new ArrangementActivityResponse(
                        l.getArrangement().getId(),
                        l.getActivity().getId(),
                        l.getActivity().getName()))
                .toList();
    }

    /** Fjern aktivitet fra arrangement. */
    @Transactional
    public void remove(Long arrangementId, Long activityId) {
        if (!repo.existsByArrangement_IdAndActivity_Id(arrangementId, activityId)) {
            throw new IllegalArgumentException("Link not found (arrangementId=" + arrangementId + ", activityId=" + activityId + ")");
        }
        repo.deleteByArrangement_IdAndActivity_Id(arrangementId, activityId);
    }
}
