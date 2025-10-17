package com.example.miniProjekt.web.dto;

/** Bekræftelse/visning af koblingen. */
public record ActivityEquipmentResponse(
        Long activityId,
        Long equipmentId,
        String activityName,
        String equipmentName
) {}
