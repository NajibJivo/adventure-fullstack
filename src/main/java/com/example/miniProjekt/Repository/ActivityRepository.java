package com.example.miniProjekt.Repository;

import com.example.miniProjekt.Model.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByMinAgeLessThanEqual(Integer age);

    List<Activity> findByEquipmentRequiredTrue();

    List<Activity> findByMaxParticipantsGreaterThanEqual(Integer participants);

    // Custom query - find aktiviteter passende for alder og deltagerantal
    @Query("SELECT a FROM Activity a WHERE a.minAge <= :age AND a.maxParticipants >= :participants")
    List<Activity> findSuitableActivities(@Param("age") Integer age, @Param("participants") Integer participants);
}
