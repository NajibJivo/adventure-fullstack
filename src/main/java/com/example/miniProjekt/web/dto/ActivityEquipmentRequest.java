package com.example.miniProjekt.web.dto;

/** Tilføj/afmeld udstyr på en aktivitet. */
public record ActivityEquipmentRequest(
        Long activityId,
        Long equipmentId
) {}
