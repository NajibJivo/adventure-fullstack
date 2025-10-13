package com.example.miniProjekt.advice;

import com.example.miniProjekt.service.exceptions.ActivityNotFoundException;
import com.example.miniProjekt.service.exceptions.BookingNotFoundException;
import com.example.miniProjekt.service.exceptions.CustomerNotFoundException;
import com.example.miniProjekt.service.exceptions.ProductNotFoundException;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            BookingNotFoundException.class,
            CustomerNotFoundException.class,
            ProductNotFoundException.class
    })
    public ResponseEntity<String> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ActivityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> activityNotFoundException(ActivityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of(
                        "timestamp", Instant.now().toString(),
                        "status", 404,
                        "error", "Not Found",
                        "message", ex.getMessage()
                )
        );
    }
}
