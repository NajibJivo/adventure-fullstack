package com.example.miniProjekt.web.dto;

/** Tilføj/afmeld udstyr på en aktivitet. */
public record ActivityEquipmentRequest(
        Long activityId,     // FK -> Activity
        Long equipmentId,    // FK -> Equipment
        Integer quantity     // antal af udstyret til aktiviteten (>= 1)
) {}
