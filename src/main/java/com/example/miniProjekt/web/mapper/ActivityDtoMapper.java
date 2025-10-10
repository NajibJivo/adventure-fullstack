package com.example.miniProjekt.web.mapper;

import com.example.miniProjekt.model.Activity;
import com.example.miniProjekt.web.dto.ActivityRequest;
import com.example.miniProjekt.web.dto.ActivityResponse;

public final class ActivityDtoMapper {
    private ActivityDtoMapper() {}

    public static void copy(ActivityRequest r, Activity target) {
        target.setName(r.name());
        target.setDescription(r.description());
        target.setPrice(r.price());
        target.setDuration(r.duration());
        target.setMinAge(r.minAge());
        target.setMinHeight(r.minHeight());
        target.setAvailableFrom(r.availableFrom());
        target.setAvailableTo(r.availableTo());
        target.setImageUrl(r.imageUrl());
    }

    public static ActivityResponse toResponse(Activity a) {
        return new ActivityResponse(
                a.getId(),
                a.getName(),
                a.getDescription(),
                a.getPrice(),
                a.getDuration(),
                a.getMinAge(),
                a.getMinHeight(),
                a.getAvailableFrom(),
                a.getAvailableTo(),
                a.getImageUrl()
        );
    }
}
