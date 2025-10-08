package com.example.miniProjekt.web.dto;

import com.example.miniProjekt.model.BookingStatus;

import java.time.LocalDateTime;

public record BookingResponse(
        Long id,
        Long activityId,
        Long customerId,
        LocalDateTime startDateTime,
        Integer participants,
        BookingStatus bookingStatus,
        String instructorName
) {}
