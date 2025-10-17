package com.example.miniProjekt.web.dto;

/** Linje i et salg (request). unitPrice udelades typisk og beregnes i service. */
public record SaleLineItemRequest(
        Long productId,
        Integer quantity
) {}
