package com.example.miniProjekt.web.dto;

import java.time.LocalDateTime;
import java.util.List;

/** Create/update Sale.
 *  customerId kan være null (kontantsalg).
 *  unitPrice sættes normalt fra Product.price i service-laget.
 */
public record SaleRequest(
        LocalDateTime saleDateTime,
        Long customerId,
        List<SaleLineItemRequest> lines
) {}
