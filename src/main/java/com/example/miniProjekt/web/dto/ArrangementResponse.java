package com.example.miniProjekt.web.dto;

import java.time.LocalDateTime;

/** Response DTO returned to clients for Arrangement (matches ERD).**/
public record ArrangementResponse(
        Long id,
        Long customerId,
        String title,
        LocalDateTime eventDate,
        String notes,
        Integer maxParticipants
) {}
