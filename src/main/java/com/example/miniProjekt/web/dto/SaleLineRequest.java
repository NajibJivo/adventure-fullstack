package com.example.miniProjekt.web.dto;

import java.math.BigDecimal;

/** Linje i et salg (request). unitPrice udelades typisk og beregnes i service. */
public record SaleLineRequest(
        Long productId,
        Integer quantity,
        BigDecimal unitPrice
) {}
