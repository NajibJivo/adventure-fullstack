package com.example.miniProjekt.web.dto;

import java.time.LocalDate;

public record EquipmentResponse(
        Long id,
        String equipmentName,
        LocalDate maintenanceDate
) {}
