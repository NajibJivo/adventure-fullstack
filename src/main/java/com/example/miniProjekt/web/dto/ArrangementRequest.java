package com.example.miniProjekt.web.dto;

import java.time.LocalDateTime;

/** Request DTO for creating/updating an Arrangement (matches ERD). */
public record ArrangementRequest(
        Long customerId,
        String title,
        LocalDateTime eventDate,
        String notes,
        Integer maxParticipants
) {}
