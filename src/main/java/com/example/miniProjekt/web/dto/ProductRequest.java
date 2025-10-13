package com.example.miniProjekt.web.dto;

import java.math.BigDecimal;

public record ProductRequest(
        String name,
        BigDecimal price,
        Boolean isActive
) {}
