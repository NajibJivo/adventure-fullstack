package com.example.miniProjekt.web.dto;

import java.math.BigDecimal;

/** Læsemodel for salgs-linje med den historiske unitPrice. */
public record SaleLineItemResponse (
        BigDecimal lineTotal
){}
