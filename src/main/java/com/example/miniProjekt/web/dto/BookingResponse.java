package com.example.miniProjekt.web.dto;

import com.example.miniProjekt.model.BookingStatus;

import java.time.LocalDateTime;
/**
 * BookingResponse – output-DTO der sendes fra API’et til klienten.
 * Indeholder kun de felter, UI’et skal bruge (ingen JPA-relationsobjekter).
 * Immutabilitet: Java records = simple, sikre, uforanderlige containers til I/O.
 */
public record BookingResponse(
        Long id,
        Long activityId,
        Long customerId,
        LocalDateTime startDateTime,
        Integer participants,
        BookingStatus bookingStatus,
        String instructorName
) {}
