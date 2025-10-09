package com.example.miniProjekt.web.dto;

import com.example.miniProjekt.model.UserRole;
/**
 * CustomerRequest – input-DTO til oprettelse/opdatering af Customer.
 * Bruges ved POST/PUT; service håndhæver unik email.
 */
public record CustomerRequest(
        String name,
        String phone,
        String email,
        UserRole userRole
) {}
