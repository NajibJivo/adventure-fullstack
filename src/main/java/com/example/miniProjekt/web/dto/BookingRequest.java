package com.example.miniProjekt.web.dto;

import com.example.miniProjekt.model.BookingStatus;

import java.time.LocalDateTime;

public record BookingRequest(
        Long activityId,
        Long customerId,
        LocalDateTime startDatetime,
        Integer participants,
        BookingStatus bookingStatus, // send fx EDIT ved oprettelse
        String instructorName
) {}
