package com.example.miniProjekt.service.exceptions;

import com.example.miniProjekt.model.Sale;

public class SaleNotFoundException extends RuntimeException {
    public SaleNotFoundException( Long id) {
        super("Sale not found with id: " + id);
    }
}
