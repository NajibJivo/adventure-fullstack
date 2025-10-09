package com.example.miniProjekt.web.dto;

import com.example.miniProjekt.model.UserRole;
/**
 * CustomerResponse – output-DTO sendt til klienten.
 */
public record CustomerResponse(
        Long id,
        String name,
        String phone,
        String email,
        UserRole userRole
){}
