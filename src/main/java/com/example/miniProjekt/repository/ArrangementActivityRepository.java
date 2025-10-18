package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.ArrangementActivity;
import com.example.miniProjekt.model.ArrangementActivityId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/** Repo for arrangement_activity join. */
public interface ArrangementActivityRepository extends JpaRepository<ArrangementActivity, ArrangementActivityId> {
    List<ArrangementActivity> findByArrangement_Id(Long arrangementId);
    boolean existsByArrangement_IdAndActivity_Id(Long arrangementId, Long activityId);
    void deleteByArrangement_IdAndActivity_Id(Long arrangementId, Long activityId);

}
