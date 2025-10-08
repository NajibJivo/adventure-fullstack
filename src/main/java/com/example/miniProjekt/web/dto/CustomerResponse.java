package com.example.miniProjekt.web.dto;

import com.example.miniProjekt.model.UserRole;

public record CustomerResponse(
        Long id,
        String name,
        String phone,
        String email,
        UserRole userRole
){}
