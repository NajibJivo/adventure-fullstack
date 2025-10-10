package com.example.miniProjekt.web.dto;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String name,
        BigDecimal price,
        Boolean isActive
) {}
