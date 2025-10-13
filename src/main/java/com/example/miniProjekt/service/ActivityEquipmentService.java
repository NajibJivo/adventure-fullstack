package com.example.miniProjekt.service;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.model.ActivityEquipment;
import com.example.miniProjekt.model.Equipment;
import com.example.miniProjekt.repository.ActivityEquipmentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ActivityEquipmentService {
    private final ActivityEquipmentRepository eRepo;
    private final ActivityServiceDto activityService;
    private final EquipmentService equipmentService;

    public ActivityEquipmentService(ActivityEquipmentRepository eRepo,
                                    ActivityServiceDto activityService,
                                    EquipmentService equipmentService) {
        this.eRepo = eRepo;
        this.activityService = activityService;
        this.equipmentService = equipmentService;
    }

    /** LIST: alt udstyr på en aktivitet */
    @Transactional(readOnly = true)
    public List<Equipment> listEquipmentForActivity(Long activityId) {
        return eRepo.findByActivity_Id(activityId).stream()
                .map(ActivityEquipment::getEquipment)
                .toList();
    }

    /** ADD: tilføj ét equipment til en aktivitet (idempotent mht. dubletter) */
    @Transactional
    public void addEquipmentToActivity(Long activityId, Long equipmentId) {
        // sikrer at begge findes (genbrugker services’ getByIdOrThrow)
        Activity activity = activityService.getByIdOrThrow(activityId);
        Equipment equipment = equipmentService.getByIdOrThrow(equipmentId);

        // undgå dubletter for pænere fejl end DB 500
        if (eRepo.existsByActivity_IdAndEquipment_Id(activityId, equipmentId)) {
            throw new DataIntegrityViolationException(
                    "Equipment already assigned to activity");
        }

        ActivityEquipment link = new ActivityEquipment(activity, equipment);
        eRepo.save(link);
    }

    /** REMOVE: fjern koblingen (no-op hvis den ikke findes) */
    @Transactional
    public void removeEquipmentFromActivity(Long activityId, Long equipmentId) {
        eRepo.deleteByActivity_IdAndEquipment_Id(activityId, equipmentId);
    }
}
