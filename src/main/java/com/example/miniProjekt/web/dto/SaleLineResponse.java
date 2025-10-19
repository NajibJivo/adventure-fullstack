package com.example.miniProjekt.web.dto;

import java.math.BigDecimal;

/**
 * Response-DTO for en salgs-linje.
 */
public record SaleLineResponse(
        Long id,
        Long saleId,
        Long productId,
        String productName,
        Integer quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
