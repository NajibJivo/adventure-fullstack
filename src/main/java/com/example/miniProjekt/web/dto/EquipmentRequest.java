package com.example.miniProjekt.web.dto;

import java.time.LocalDate;

/**
 * Request DTO for creating/updating Equipment.
 * Keeps web layer decoupled from JPA entity by using primitives/values only.
 */
public record EquipmentRequest(
        String equipmentName,
        LocalDate maintenanceDate
) {}
