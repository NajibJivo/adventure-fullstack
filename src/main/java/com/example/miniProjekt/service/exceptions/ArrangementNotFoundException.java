package com.example.miniProjekt.service.exceptions;

public class ArrangementNotFoundException extends RuntimeException {
    public ArrangementNotFoundException(Long id) {
        super("Could not find arrangement with id " + id);
    }
}
