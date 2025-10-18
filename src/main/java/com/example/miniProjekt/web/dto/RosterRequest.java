package com.example.miniProjekt.web.dto;

import java.time.LocalDate;

/** Opret/opdater en vagt (employeeId refererer til Customer (EMPLOYEE)). */
public record RosterRequest(
        Long employeeId, // required (Customer med role=EMPLOYEE)
        LocalDate workDate // required (YYYY-MM-DD)
) {}
