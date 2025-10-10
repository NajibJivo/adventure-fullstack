package com.example.miniProjekt.web.dto;

public record SignupRequest(
        String username,
        String password,
        String name,
        String email,
        String phone
) {}
