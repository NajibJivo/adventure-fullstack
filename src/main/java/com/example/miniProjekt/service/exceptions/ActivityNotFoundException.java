package com.example.miniProjekt.service.exceptions;

public class ActivityNotFoundException extends RuntimeException {
    public ActivityNotFoundException(Long id) {
        super("Activity not found: " + id);
    }
}
