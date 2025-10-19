package com.example.miniProjekt.web.dto;

import com.example.miniProjekt.model.BookingStatus;

import java.time.LocalDateTime;
/**
 * BookingRequest – input-DTO til oprettelse/opdatering af booking.
 * Bruges af BookingController ved POST/PUT.
 *  Kontrakt:
 *   - activityId: påkrævet ved create (FK til Activity)
 *   - customerId: påkrævet ved create (FK til Customer)
 *   - + de andre felter
 **/
public record BookingRequest(
        Long activityId,
        Long customerId,
        LocalDateTime startDateTime,
        Integer participants,
        BookingStatus bookingStatus, // send fx EDIT ved oprettelse
        String instructorName
) {}
