package com.example.miniProjekt.web.dto;

import com.example.miniProjekt.model.UserRole;

public record CustomerRequest(
        String name,
        String phone,
        String email,
        UserRole userRole
) {}
