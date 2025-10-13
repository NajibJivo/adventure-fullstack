package com.example.miniProjekt.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ActivityResponse(
        Long id,
        String name,
        String description,
        BigDecimal price,
        Integer duration,
        Integer minAge,
        Integer minHeight,
        LocalDateTime availableFrom,
        LocalDateTime availableTo,
        String imageUrl
) {}
