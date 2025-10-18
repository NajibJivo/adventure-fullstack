package com.example.miniProjekt.repository;

import com.example.miniProjekt.model.ActivityEquipment;
import com.example.miniProjekt.model.ActivityEquipmentId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ActivityEquipmentRepository extends JpaRepository<ActivityEquipment, ActivityEquipmentId > {
    List<ActivityEquipment> findByActivity_Id(Long activityId);
    List<ActivityEquipment> findByEquipment_Id(Long equipmentId);
    boolean existsByActivity_IdAndEquipment_Id(Long activityId, Long equipmentId);
    Optional<ActivityEquipment> findByActivity_IdAndEquipment_Id(Long activityId, Long equipmentId);
    // Alternativt kan vi også bruge:
    // boolean existsByIdActivityIdAndIdEquipmentId(Long activityId, Long equipmentId);
}
