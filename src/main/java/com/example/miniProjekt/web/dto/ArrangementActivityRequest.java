package com.example.miniProjekt.web.dto;


/** Tilføj/afmeld en activity til/fra et arrangement. */
public record ArrangementActivityRequest(
        Long arrangementId,
        Long activityId
) {}
