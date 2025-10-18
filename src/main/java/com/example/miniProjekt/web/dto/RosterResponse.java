package com.example.miniProjekt.web.dto;

import java.time.LocalDate;

/** Læsemodel for en vagt. */
public record RosterResponse(
        Long id,
        Long employeeId,
        LocalDate workDate
) {}
