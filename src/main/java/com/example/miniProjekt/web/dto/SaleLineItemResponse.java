package com.example.miniProjekt.web.dto;

import java.math.BigDecimal;

/** Læsemodel for salgs-linje med den historiske unitPrice. */
public record SaleLineItemResponse (
        Long id,
        Long productId,
        String name,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
){}
