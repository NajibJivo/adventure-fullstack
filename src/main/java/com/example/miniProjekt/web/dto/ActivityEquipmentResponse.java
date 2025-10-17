package com.example.miniProjekt.web.dto;

/** Bekræftelse/visning af koblingen. */
public record ActivityEquipmentResponse(
        Long id,
        Long activityId,
        String activityName,
        Long equipmentId,
        String equipmentName,
        Integer quantity// antal af udstyret til aktiviteten (>= 1)
) {}
