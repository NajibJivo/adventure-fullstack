package com.example.miniProjekt.web.dto;

/** Bekræftelse/visning af koblingen. */
public record ArrangementActivityResponse(
        Long arrangementId,
        Long activityId,
        String arrangementTitle,
        String activityName
) {}
