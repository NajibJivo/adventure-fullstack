package com.example.miniProjekt.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Læsemodel af et salg inkl. beregnet total. */
public record SaleResponse(
        Long id,
        LocalDateTime saleDateTime,
        Long customerId,            // kan være null
        BigDecimal total,           // sum(lines.lineTotal)
        List<SaleLineItemResponse> lines
) {}
